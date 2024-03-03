package com.learn.machinelearningandroid.tflite

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import androidx.camera.core.ImageProxy
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.learn.machinelearningandroid.R
import org.tensorflow.lite.DataType
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.gms.vision.detector.Detection
import org.tensorflow.lite.task.gms.vision.detector.ObjectDetector

class ObjectDetectorHelper(
    val context: Context,
    val detectorListener: DetectorListener?,
    private var threshold: Float = 0.5F,
    private var maxResults: Int = 5,
    private val modelName: String = "efficientdet_lite0_v1.tflite",
) {
    private var objectDetector: ObjectDetector? = null

    init {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { isGpuAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (isGpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLiteVision.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            setupObjectDetector()
        }.addOnFailureListener {
            detectorListener?.onError(context.getString(R.string.tflitevision_is_not_initialized_yet))
        }
    }

    private fun setupObjectDetector() {
        val optionsBuilder = ObjectDetector.ObjectDetectorOptions
            .builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
        val baseOptionsBuilder = BaseOptions.builder()

        if (CompatibilityList().isDelegateSupportedOnThisDevice) {
            baseOptionsBuilder.useGpu()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            baseOptionsBuilder.useNnapi()
        } else {
            // Menggunakan CPU
            baseOptionsBuilder.setNumThreads(4)
        }

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            objectDetector = ObjectDetector.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            detectorListener?.onError(context.getString(R.string.image_classifier_failed))
        }
    }

    fun detectObject(image: ImageProxy) {
        if (TfLiteVision.isInitialized().not()) {
            val errorMessage = context.getString(R.string.tflitevision_is_not_initialized_yet)
            detectorListener?.onError(errorMessage)
            return
        }

        if (objectDetector == null) {
            setupObjectDetector()
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(Rot90Op(-image.imageInfo.rotationDegrees / 90))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(toBitmap(image)))

        var inferenceTime = SystemClock.uptimeMillis()
        val results = objectDetector?.detect(tensorImage)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        detectorListener?.onResults(
            results,
            inferenceTime,
            tensorImage.width,
            tensorImage.height,
        )
    }

    private fun toBitmap(image: ImageProxy): Bitmap {
        val bitmapBuffer = Bitmap.createBitmap(
            image.width,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
        image.close()
        return bitmapBuffer
    }

    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int,
        )
    }
}