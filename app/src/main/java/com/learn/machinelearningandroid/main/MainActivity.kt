package com.learn.machinelearningandroid.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.learn.machinelearningandroid.camera.CameraActivity
import com.learn.machinelearningandroid.customview.CustomViewActivity
import com.learn.machinelearningandroid.databinding.ActivityMainBinding
import com.learn.machinelearningandroid.generativeai.smartreply.ChatActivity
import com.learn.machinelearningandroid.imageclassification.ImageClassificationActivity
import com.learn.machinelearningandroid.mediapipe.AudioClassificationActivity
import com.learn.machinelearningandroid.mediapipe.TextClassificationActivity
import com.learn.machinelearningandroid.mlkit.MLKitRecognitionActivity
import com.learn.machinelearningandroid.tflite.PredictionActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
    }

    private fun initListeners() {
        binding.apply {
            btnCustomView.setOnClickListener {
                navigateToCustomViewActivity()
            }

            btnImageClassification.setOnClickListener {
                navigateToImageClassification()
            }

            btnMlkitRecognition.setOnClickListener {
                navigateToMLKitTextRecognition()
            }

            btnImageClassificationTflite.setOnClickListener {
                navigateToImageClassificationTFLiteCamera()
            }

            btnObjectDetectionTflite.setOnClickListener {
                navigateToObjectDetectionTFLiteCamera()
            }

            btnPredictionTflite.setOnClickListener {
                navigateToPredictionTfLite()
            }

            btnImageClassificationMediaPipe.setOnClickListener {
                navigateToImageClassificationMediaPipeCamera()
            }

            btnAudioClassificationMediaPipe.setOnClickListener {
                navigateToAudioClassificationMediaPipe()
            }

            btnTextClassificationMediaPipe.setOnClickListener {
                navigateToTextClassificationMediaPipe()
            }

            btnSmartReplyGenerativeAi.setOnClickListener {
                navigateToSmartReplyGenerativeAI()
            }
        }
    }

    private fun navigateToCustomViewActivity() {
        startActivity(Intent(this, CustomViewActivity::class.java))
    }

    private fun navigateToImageClassification() {
        startActivity(Intent(this, ImageClassificationActivity::class.java))
    }

    private fun navigateToMLKitTextRecognition() {
        startActivity(Intent(this, MLKitRecognitionActivity::class.java))
    }

    private fun navigateToImageClassificationTFLiteCamera() {
        Intent(this, CameraActivity::class.java).apply {
            putExtra(CameraActivity.CAMERA_TYPE_KEY, CameraActivity.IMAGE_CLASSIFICATION)
            startActivity(this)
        }
    }

    private fun navigateToObjectDetectionTFLiteCamera() {
        Intent(this, CameraActivity::class.java).apply {
            putExtra(CameraActivity.CAMERA_TYPE_KEY, CameraActivity.OBJECT_DETECTION)
            startActivity(this)
        }
    }

    private fun navigateToPredictionTfLite() {
        startActivity(Intent(this, PredictionActivity::class.java))
    }

    private fun navigateToImageClassificationMediaPipeCamera() {
        Intent(this, CameraActivity::class.java).apply {
            putExtra(CameraActivity.CAMERA_TYPE_KEY, CameraActivity.IMAGE_CLASSIFICATION_MEDIA_PIPE)
            startActivity(this)
        }
    }

    private fun navigateToAudioClassificationMediaPipe() {
        startActivity(Intent(this, AudioClassificationActivity::class.java))
    }

    private fun navigateToTextClassificationMediaPipe() {
        startActivity(Intent(this, TextClassificationActivity::class.java))
    }

    private fun navigateToSmartReplyGenerativeAI() {
        startActivity(Intent(this, ChatActivity::class.java))
    }
}