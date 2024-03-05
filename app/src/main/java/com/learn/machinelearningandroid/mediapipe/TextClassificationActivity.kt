package com.learn.machinelearningandroid.mediapipe

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mediapipe.tasks.components.containers.Classifications
import com.learn.machinelearningandroid.databinding.ActivityTextClassificationBinding
import java.text.NumberFormat

class TextClassificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTextClassificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextClassificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textClassifierHelper = TextClassifierHelper(
            context = this,
            classifierListener = object : TextClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    Toast.makeText(this@TextClassificationActivity, error, Toast.LENGTH_LONG).show()
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        results?.let { it ->
                            if (it.isNotEmpty() && it[0].categories().isNotEmpty()) {
                                val sortedCategories =
                                    it[0].categories().sortedByDescending { it?.score() }

                                val displayResult =
                                    sortedCategories.joinToString("\n") {
                                        "${it.categoryName()} " + NumberFormat.getPercentInstance()
                                            .format(it.score()).trim()
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

        binding.btnClassify.setOnClickListener {
            val inputText = binding.edInput.text.toString()
            textClassifierHelper.classify(inputText)
        }
    }
}