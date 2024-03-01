package com.learn.machinelearningandroid.mlkit

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.learn.machinelearningandroid.databinding.ActivityMlkitResultBinding

class MLKitResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMlkitResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMlkitResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        val detectedText = intent.getStringExtra(EXTRA_RESULT)

        val imageUri = Uri.parse(imageUriString)

        imageUri?.let {
            binding.resultImage.setImageURI(it)
        }

        binding.resultText.text = detectedText

    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}