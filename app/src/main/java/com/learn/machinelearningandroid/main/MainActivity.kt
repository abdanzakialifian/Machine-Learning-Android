package com.learn.machinelearningandroid.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.learn.machinelearningandroid.customview.CustomViewActivity
import com.learn.machinelearningandroid.databinding.ActivityMainBinding
import com.learn.machinelearningandroid.imageclassification.ImageClassificationActivity

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
        }
    }

    private fun navigateToCustomViewActivity() {
        startActivity(Intent(this, CustomViewActivity::class.java))
    }

    private fun navigateToImageClassification() {
        startActivity(Intent(this, ImageClassificationActivity::class.java))
    }
}