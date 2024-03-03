package com.learn.machinelearningandroid.tflite

import android.content.Context
import android.content.res.AssetManager
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.google.android.gms.tflite.java.TfLite
import com.learn.machinelearningandroid.R
import okio.IOException
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegateFactory
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class PredictionHelper(
    val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val modelName: String = "rice_stock.tflite",
) {
    private var interpreter: InterpreterApi? = null

    init {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { isGpuDelegateAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (isGpuDelegateAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLite.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            loadLocalModel()
        }.addOnFailureListener {
            onError(context.getString(R.string.tflite_is_not_initialized_yet))
        }
    }

    fun predict(input: String) {
        if (interpreter == null) {
            return
        }

        val inputArray = FloatArray(1)
        inputArray[0] = input.toFloat()
        val outputArray = Array(1) { FloatArray(1) }
        try {
            interpreter?.run(inputArray, outputArray)
            onResult(outputArray[0][0].toString())
        } catch (e: Exception) {
            onError(context.getString(R.string.no_tflite_interpreter_loaded))
        }
    }

    private fun loadLocalModel() {
        try {
            val buffer: ByteBuffer = loadModelFile(context.assets, modelName)
            initializeInterpreter(buffer)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        assetManager.openFd(modelPath).use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                val fileChannel = inputStream.channel
                val startOffset = fileDescriptor.startOffset
                val declaredLength = fileDescriptor.declaredLength
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            }
        }
    }

    private fun initializeInterpreter(model: Any) {
        interpreter?.close()
        try {
            val options = InterpreterApi.Options()
                .setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY)
            if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                options.addDelegateFactory(GpuDelegateFactory())
            }
            if (model is ByteBuffer) {
                interpreter = InterpreterApi.create(model, options)
            }
        } catch (e: Exception) {
            onError(e.message.toString())
        }
    }
}