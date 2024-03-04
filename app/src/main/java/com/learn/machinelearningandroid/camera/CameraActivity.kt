package com.learn.machinelearningandroid.camera

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.learn.machinelearningandroid.databinding.ActivityCameraBinding
import com.learn.machinelearningandroid.mediapipe.MediaPipeImageClassifierHelper
import com.learn.machinelearningandroid.tflite.ImageClassifierHelper
import com.learn.machinelearningandroid.tflite.ObjectDetectorHelper
import com.learn.machinelearningandroid.utils.createCustomTempFile
import org.tensorflow.lite.task.gms.vision.classifier.Classifications
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.text.NumberFormat
import java.util.concurrent.Executors
import com.google.mediapipe.tasks.components.containers.Classifications as ClassificationsMediaPipe

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private lateinit var barcodeScanner: BarcodeScanner
    private var isFirstCall = true
    private var cameraType: String? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var mediaPipeImageClassifierHelper: MediaPipeImageClassifierHelper
    private lateinit var objectDetectorHelper: ObjectDetectorHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.switchCamera.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }
        binding.captureImage.setOnClickListener { takePhoto() }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        cameraType = intent.getStringExtra(CAMERA_TYPE_KEY)
        startCamera()
    }

    private fun startCamera() {
        when (cameraType) {
            BARCODE_SCANNER -> {
                barcodeScannerConfiguration()
            }

            IMAGE_CLASSIFICATION -> {
                imageClassificationConfiguration()
            }

            OBJECT_DETECTION -> {
                objectDetectionConfiguration()
            }

            IMAGE_CLASSIFICATION_MEDIA_PIPE -> {
                imageClassificationMediaPipeConfiguration()
            }

            else -> {
                cameraConfiguration()
            }
        }
    }

    private fun barcodeScannerConfiguration() {
        val options =
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        val analyzer = MlKitAnalyzer(
            listOf(barcodeScanner),
            COORDINATE_SYSTEM_VIEW_REFERENCED,
            ContextCompat.getMainExecutor(this)
        ) { result ->
            showResult(result)
        }

        val cameraController = LifecycleCameraController(baseContext)
        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(this),
            analyzer
        )
        cameraController.bindToLifecycle(this)
        binding.apply {
            viewFinder.controller = cameraController
            switchCamera.visibility = View.GONE
            captureImage.visibility = View.GONE
        }
    }

    private fun imageClassificationConfiguration() {
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object :
                ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@CameraActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        results?.let { result ->
                            if (result.isNotEmpty() && result[0].categories.isNotEmpty()) {
                                val sortedCategories =
                                    result[0].categories.sortedByDescending { it.score }
                                val displayResult = sortedCategories.joinToString("\n") {
                                    StringBuilder().append(it.label).append(" ").append(
                                        NumberFormat.getPercentInstance().format(it.score).trim()
                                    )
                                }
                                binding.tvResult.text = displayResult
                                binding.tvInferenceTime.text =
                                    StringBuilder().append(inferenceTime).append(" ms")
                            } else {
                                binding.tvResult.text = ""
                                binding.tvInferenceTime.text = ""
                            }
                        }
                    }
                }
            }
        )
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()
            val imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also { imageAnalysis ->
                    imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                        imageClassifierHelper.classifyImage(image)
                    }
                }
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalyzer,
                )

            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
        binding.apply {
            tvInferenceTime.visibility = View.VISIBLE
            tvResult.visibility = View.VISIBLE
            switchCamera.visibility = View.GONE
            captureImage.visibility = View.GONE
        }
    }

    private fun imageClassificationMediaPipeConfiguration() {
        mediaPipeImageClassifierHelper = MediaPipeImageClassifierHelper(
            context = this,
            classifierListener = object :
                MediaPipeImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@CameraActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResults(results: List<ClassificationsMediaPipe>?, inferenceTime: Long) {
                    runOnUiThread {
                        results?.let { result ->
                            if (result.isNotEmpty() && result[0].categories().isNotEmpty()) {
                                val sortedCategories =
                                    result[0].categories().sortedByDescending { it.score() }
                                val displayResult = sortedCategories.joinToString("\n") {
                                    StringBuilder().append(it.categoryName()).append(" ").append(
                                        NumberFormat.getPercentInstance().format(it.score()).trim()
                                    )
                                }
                                binding.tvResult.text = displayResult
                                binding.tvInferenceTime.text =
                                    StringBuilder().append(inferenceTime).append(" ms")
                            } else {
                                binding.tvResult.text = ""
                                binding.tvInferenceTime.text = ""
                            }
                        }
                    }
                }
            }
        )
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()
            val imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also { imageAnalysis ->
                    imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                        mediaPipeImageClassifierHelper.classifyImage(image)
                    }
                }
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalyzer,
                )

            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
        binding.apply {
            tvInferenceTime.visibility = View.VISIBLE
            tvResult.visibility = View.VISIBLE
            switchCamera.visibility = View.GONE
            captureImage.visibility = View.GONE
        }
    }

    private fun objectDetectionConfiguration() {
        objectDetectorHelper = ObjectDetectorHelper(
            context = this,
            detectorListener = object :
                ObjectDetectorHelper.DetectorListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@CameraActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResults(
                    results: MutableList<Detection>?,
                    inferenceTime: Long,
                    imageHeight: Int,
                    imageWidth: Int,
                ) {
                    runOnUiThread {
                        results?.let { result ->
                            if (result.isNotEmpty() && result[0].categories.isNotEmpty()) {
                                Log.d("CEK", result.toString())
                                binding.overlay.setResults(results, imageWidth, imageHeight)
                                binding.tvInferenceTime.text =
                                    StringBuilder().append(inferenceTime).append(" ms")
                            } else {
                                binding.overlay.clear()
                                binding.tvInferenceTime.text = ""
                            }
                        }
                        binding.overlay.invalidate()
                    }
                }
            }
        )
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()
            val imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also { imageAnalysis ->
                    imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                        objectDetectorHelper.detectObject(image)
                    }
                }
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer,
                )

            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
        binding.apply {
            tvInferenceTime.visibility = View.VISIBLE
            overlay.visibility = View.VISIBLE
            switchCamera.visibility = View.GONE
            captureImage.visibility = View.GONE
        }
    }

    private fun cameraConfiguration() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun showResult(result: MlKitAnalyzer.Result?) {
        if (isFirstCall) {
            val barcodeResults = result?.getValue(barcodeScanner)
            if ((barcodeResults != null) && (barcodeResults.size != 0) && (barcodeResults.first() != null)) {
                isFirstCall = false
                val barcode = barcodeResults.getOrNull(0)
                val alertDialog = AlertDialog.Builder(this)
                    .setMessage(barcode?.rawValue)
                    .setPositiveButton("Buka") { _, _ ->
                        isFirstCall = true
                        when (barcode?.valueType) {
                            Barcode.TYPE_URL -> {
                                val openBrowserIntent = Intent(Intent.ACTION_VIEW)
                                openBrowserIntent.data = Uri.parse(barcode.url?.url)
                                startActivity(openBrowserIntent)
                            }

                            else -> {
                                Toast.makeText(this, "Unsupported data type", Toast.LENGTH_SHORT)
                                    .show()
                                startCamera()
                            }
                        }
                    }
                    .setNegativeButton("Scan lagi") { _, _ ->
                        isFirstCall = true
                    }
                    .setCancelable(false)
                    .create()
                alertDialog.show()
            }
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createCustomTempFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val intent = Intent()
                    intent.putExtra(EXTRA_CAMERAX_IMAGE, output.savedUri.toString())
                    setResult(CAMERAX_RESULT, intent)
                    finish()
                }

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Gagal mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "onError: ${exc.message}")
                }
            }
        )
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture?.targetRotation = rotation
            }
        }
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

    companion object {
        private const val TAG = "CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val CAMERAX_RESULT = 200
        const val CAMERA_TYPE_KEY = "camera_type_key"
        const val BARCODE_SCANNER = "barcode_scanner"
        const val IMAGE_CLASSIFICATION = "image_classification"
        const val OBJECT_DETECTION = "object_detection"
        const val IMAGE_CLASSIFICATION_MEDIA_PIPE = "image_classification_media_pipe"
    }
}