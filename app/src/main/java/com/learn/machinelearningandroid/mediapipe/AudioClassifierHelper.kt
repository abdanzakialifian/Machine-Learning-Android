package com.learn.machinelearningandroid.mediapipe

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.os.SystemClock
import com.google.mediapipe.tasks.audio.audioclassifier.AudioClassifier
import com.google.mediapipe.tasks.audio.audioclassifier.AudioClassifierResult
import com.google.mediapipe.tasks.audio.core.RunningMode
import com.google.mediapipe.tasks.components.containers.AudioData
import com.google.mediapipe.tasks.components.containers.AudioData.AudioDataFormat
import com.google.mediapipe.tasks.components.containers.Classifications
import com.google.mediapipe.tasks.core.BaseOptions
import com.learn.machinelearningandroid.R
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class AudioClassifierHelper(
    val context: Context,
    val classifierListener: ClassifierListener,
    private val threshold: Float = 0.1F,
    private val maxResults: Int = 3,
    private val modelName: String = "yamnet.tflite",
    private val runningMode: RunningMode = RunningMode.AUDIO_STREAM,
    private val overlap: Float = 0.5F,
) {
    private var audioClassifier: AudioClassifier? = null
    private var recorder: AudioRecord? = null
    private var executor: ScheduledThreadPoolExecutor? = null

    init {
        initClassifier()
    }

    private fun initClassifier() {
        try {
            val optionsBuilder = AudioClassifier.AudioClassifierOptions.builder()
                .setScoreThreshold(threshold)
                .setMaxResults(maxResults)
                .setRunningMode(runningMode)

            if (runningMode == RunningMode.AUDIO_STREAM) {
                optionsBuilder
                    .setResultListener(::streamAudioResultListener)
                    .setErrorListener(::streamAudioErrorListener)
            }

            val baseOptionsBuilder = BaseOptions.builder()
                .setModelAssetPath(modelName)
            optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

            audioClassifier = AudioClassifier.createFromOptions(context, optionsBuilder.build())

            if (runningMode == RunningMode.AUDIO_STREAM) {
                recorder = audioClassifier?.createAudioRecord(
                    AudioFormat.CHANNEL_IN_DEFAULT,
                    SAMPLING_RATE_IN_HZ,
                    BUFFER_SIZE_IN_BYTES.toInt()
                )
            }
        } catch (e: IllegalStateException) {
            classifierListener.onError(context.getString(R.string.audio_classifier_failed))
        } catch (e: RuntimeException) {
            classifierListener.onError(context.getString(R.string.audio_classifier_failed))
        }
    }

    private fun streamAudioResultListener(resultListener: AudioClassifierResult) {
        classifierListener.onResults(
            resultListener.classificationResults().first().classifications(),
            resultListener.timestampMs(),
        )
    }

    private fun streamAudioErrorListener(e: RuntimeException) {
        classifierListener.onError(e.message.orEmpty())
    }

    fun startAudioClassification() {
        if (audioClassifier == null) {
            initClassifier()
        }

        if (recorder?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            return
        }

        recorder?.startRecording()
        executor = ScheduledThreadPoolExecutor(1)

        val classifyRunnable = Runnable {
            recorder?.let {
                classifyAudioAsync(it)
            }
        }

        val lengthInMillisSeconds =
            ((REQUIRE_INPUT_BUFFER_SIZE * 1.0F) / SAMPLING_RATE_IN_HZ) * 1000
        val interval = (lengthInMillisSeconds * (1 - overlap)).toLong()

        executor?.scheduleAtFixedRate(
            classifyRunnable,
            0,
            interval,
            TimeUnit.MILLISECONDS,
        )
    }

    fun stopAudioClassification() {
        executor?.shutdown()
        audioClassifier?.close()
        audioClassifier = null
        recorder?.stop()
    }

    private fun classifyAudioAsync(audioRecord: AudioRecord) {
        val audioData = AudioData.create(
            AudioDataFormat.create(recorder?.format),
            SAMPLING_RATE_IN_HZ
        )
        audioData.load(audioRecord)

        val inferenceTime = SystemClock.uptimeMillis()
        audioClassifier?.classifyAsync(audioData, inferenceTime)
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>,
            inferenceTime: Long,
        )
    }

    companion object {
        private const val SAMPLING_RATE_IN_HZ = 16000
        private const val EXPECTED_INPUT_LENGTH = 0.975F
        private const val REQUIRE_INPUT_BUFFER_SIZE = SAMPLING_RATE_IN_HZ * EXPECTED_INPUT_LENGTH
        private const val BUFFER_SIZE_FACTOR = 2
        private const val BUFFER_SIZE_IN_BYTES =
            REQUIRE_INPUT_BUFFER_SIZE * Float.SIZE_BYTES * BUFFER_SIZE_FACTOR
    }
}