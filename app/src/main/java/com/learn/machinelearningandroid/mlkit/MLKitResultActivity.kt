package com.learn.machinelearningandroid.mlkit

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.learn.machinelearningandroid.R
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

        binding.translateButton.setOnClickListener {
            binding.progressIndicator.visibility = View.VISIBLE
            translateText(detectedText)
        }
    }

    private fun translateText(detectedText: String?) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.INDONESIAN)
            .build()
        val indonesianEnglishTranslator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        indonesianEnglishTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                indonesianEnglishTranslator.translate(detectedText.orEmpty())
                    .addOnSuccessListener { translatedText ->
                        binding.translatedText.text = translatedText
                        indonesianEnglishTranslator.close()
                        binding.progressIndicator.visibility = View.GONE
                    }
                    .addOnFailureListener { exception ->
                        showToast(exception.message.orEmpty())
                        indonesianEnglishTranslator.close()
                        binding.progressIndicator.visibility = View.GONE
                    }

            }.addOnFailureListener {
                showToast(resources.getString(R.string.downloading_model_fail))
                binding.progressIndicator.visibility = View.GONE
            }

        lifecycle.addObserver(indonesianEnglishTranslator)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}