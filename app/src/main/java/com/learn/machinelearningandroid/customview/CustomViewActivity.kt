package com.learn.machinelearningandroid.customview

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Region
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.learn.machinelearningandroid.R
import com.learn.machinelearningandroid.databinding.ActivityCustomViewBinding

class CustomViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
        binding.imgView.setImageBitmap(bitmap)
        // create canvas
        val canvas = Canvas(bitmap)
        canvas.drawColor(ResourcesCompat.getColor(resources, R.color.light_blue, null))
        canvas.save()
        val paint = Paint()
        paint.color = ContextCompat.getColor(this, R.color.pink)
        // clip rectangle
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipRect(
                bitmap.width / 2 - 100F,
                bitmap.height / 2 - 100F,
                bitmap.width / 2 + 100F,
                bitmap.height / 2 + 100F,
                Region.Op.DIFFERENCE,
            )
        } else {
            canvas.clipOutRect(
                bitmap.width / 2 - 100,
                bitmap.height / 2 - 100,
                bitmap.width / 2 + 100,
                bitmap.height / 2 + 100,
            )
        }
        paint.color = ContextCompat.getColor(this, R.color.light_pink)
        // create circle
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(),
            (bitmap.height / 2).toFloat(),
            200F,
            paint,
        )
        canvas.restore()
        // create custom text
        val paintText = Paint(Paint.FAKE_BOLD_TEXT_FLAG)
        paintText.textSize = 20F
        paintText.color = ResourcesCompat.getColor(resources, R.color.white, null)
        val text = "Welcome!"
        val bounds = Rect()
        paintText.getTextBounds(text, 0, text.length, bounds)
        val x = bitmap.width / 2 - bounds.centerX()
        val y = bitmap.height / 2 - bounds.centerY()
        canvas.drawText(text, x.toFloat(), y.toFloat(), paintText)
    }
}