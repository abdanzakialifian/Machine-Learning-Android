package com.learn.machinelearningandroid.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.learn.machinelearningandroid.customview.CustomViewActivity
import com.learn.machinelearningandroid.databinding.ActivityMainBinding
import com.learn.machinelearningandroid.imageclassification.ImageClassificationActivity
import com.learn.machinelearningandroid.mlkit.MLKitTextRecognitionActivity

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

            btnMlkitTextRecognition.setOnClickListener {
                navigateToMLKitTextRecognition()
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
        startActivity(Intent(this, MLKitTextRecognitionActivity::class.java))
    }
}