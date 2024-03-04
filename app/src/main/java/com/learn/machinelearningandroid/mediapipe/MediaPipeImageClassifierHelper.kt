package com.learn.machinelearningandroid.mediapipe

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import androidx.camera.core.ImageProxy
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.components.containers.Classifications
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifier
import com.learn.machinelearningandroid.R
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.task.gms.vision.TfLiteVision

class MediaPipeImageClassifierHelper(
    val context: Context,
    val classifierListener: ClassifierListener?,
    private var threshold: Float = 0.1F,
    private var maxResults: Int = 3,
    private val runningMode: RunningMode = RunningMode.LIVE_STREAM,
    private val modelName: String = "mobilenet_v1.tflite",
) {
    private var imageClassifier: ImageClassifier? = null

    init {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { isGpuAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (isGpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLiteVision.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            setupImageClassifier()
        }.addOnFailureListener {
            classifierListener?.onError(context.getString(R.string.tflitevision_is_not_initialized_yet))
        }
    }

    private fun setupImageClassifier() {
        val optionsBuilder =
            ImageClassifier.ImageClassifierOptions.builder().setScoreThreshold(threshold)
                .setMaxResults(maxResults).setRunningMode(runningMode)

        if (runningMode == RunningMode.LIVE_STREAM) {
            optionsBuilder.setResultListener { result, _ ->
                val finishTimeMs = SystemClock.uptimeMillis()
                val inferenceTime = finishTimeMs - result.timestampMs()
                classifierListener?.onResults(
                    result.classificationResult().classifications(),
                    inferenceTime
                )
            }.setErrorListener { error ->
                classifierListener?.onError(error.message.orEmpty())
            }
        }

        val baseOptionsBuilder = BaseOptions.builder().setModelAssetPath(modelName)

        if (CompatibilityList().isDelegateSupportedOnThisDevice) {
            baseOptionsBuilder.setDelegate(Delegate.GPU)
        } else {
            baseOptionsBuilder.setDelegate(Delegate.CPU)
        }

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromOptions(
                context, optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
        }
    }

    fun classifyImage(image: ImageProxy) {
        if (TfLiteVision.isInitialized().not()) {
            val errorMessage = context.getString(R.string.tflitevision_is_not_initialized_yet)
            classifierListener?.onError(errorMessage)
            return
        }

        if (imageClassifier == null) {
            setupImageClassifier()
        }

        val mpImage = BitmapImageBuilder(toBitmap(image)).build()

        val imageProcessingOptions =
            ImageProcessingOptions.builder().setRotationDegrees(image.imageInfo.rotationDegrees)
                .build()

        val inferenceTime = SystemClock.uptimeMillis()
        imageClassifier?.classifyAsync(mpImage, imageProcessingOptions, inferenceTime)
    }

    private fun toBitmap(image: ImageProxy): Bitmap {
        val bitmapBuffer = Bitmap.createBitmap(
            image.width, image.height, Bitmap.Config.ARGB_8888
        )
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
        image.close()
        return bitmapBuffer
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?,
            inferenceTime: Long,
        )
    }
}