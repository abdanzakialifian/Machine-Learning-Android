package com.learn.machinelearningandroid.mediapipe

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.components.containers.Classifications
import com.learn.machinelearningandroid.databinding.ActivityAudioClassificationBinding
import java.text.NumberFormat

class AudioClassificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioClassificationBinding

    private lateinit var audioClassifierHelper: AudioClassifierHelper

    private var isRecording = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            val message = if (isGranted) "Permission Granted" else "Permission denied"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioClassificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
        initializeAudioClassifierHelper()
        updateButtonStates()
        requestPermissionsIfNeeded()
    }

    private fun initListeners() {
        binding.apply {
            btnStart.setOnClickListener {
                audioClassifierHelper.startAudioClassification()
                isRecording = true
                updateButtonStates()
            }
            btnStop.setOnClickListener {
                audioClassifierHelper.stopAudioClassification()
                isRecording = false
                updateButtonStates()
            }
        }

    }

    private fun updateButtonStates() {
        binding.apply {
            btnStart.isEnabled = isRecording.not()
            btnStop.isEnabled = isRecording
        }
    }

    private fun initializeAudioClassifierHelper() {
        audioClassifierHelper = AudioClassifierHelper(
            context = this,
            classifierListener = object : AudioClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    Toast.makeText(this@AudioClassificationActivity, error, Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onResults(results: List<Classifications>, inferenceTime: Long) {
                    runOnUiThread {
                        results.let {
                            if (it.isNotEmpty() && it[0].categories().isNotEmpty()) {
                                val sortedCategories =
                                    it[0].categories()
                                        .sortedByDescending { sorted -> sorted?.score() }
                                val displayResult =
                                    sortedCategories.joinToString("\n") { joinString ->
                                        StringBuilder().append(joinString.categoryName()).append(" ").append(
                                            NumberFormat.getPercentInstance()
                                                .format(joinString.score()).trim()
                                        )
                                    }
                                binding.tvResult.text = displayResult
                            } else {
                                binding.tvResult.text = ""
                            }
                        }
                    }
                }
            }
        )
    }

    private fun allPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissionsIfNeeded() {
        if (allPermissionGranted().not()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isRecording) {
            audioClassifierHelper.startAudioClassification()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::audioClassifierHelper.isInitialized) {
            audioClassifierHelper.stopAudioClassification()
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.RECORD_AUDIO
    }
}