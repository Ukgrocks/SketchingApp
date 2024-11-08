package com.example.whiteboardca3

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.RangeSlider
import java.io.OutputStream
import petrov.kristiyan.colorpicker.ColorPicker


class MainActivity : AppCompatActivity() {

    // Reference to custom DrawView
    private lateinit var paint: DrawView

    // UI elements
    private lateinit var color: ImageButton
    private lateinit var stroke: ImageButton
    private lateinit var undo: ImageButton
    private lateinit var rangeSlider: RangeSlider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        paint = findViewById(R.id.draw_view)
        rangeSlider = findViewById(R.id.rangebar)
        undo = findViewById(R.id.btn_undo)
        color = findViewById(R.id.btn_color)
        stroke = findViewById(R.id.btn_stroke)

        // Undo button - removes the last stroke
        undo.setOnClickListener {
            paint.undo()
        }

        // Color picker button
        color.setOnClickListener {
            val colorPicker = ColorPicker(this@MainActivity)
            colorPicker.setOnFastChooseColorListener(object : ColorPicker.OnFastChooseColorListener {
                override fun setOnFastChooseColorListener(position: Int, color: Int) {
                    paint.setColor(color)
                }

                override fun onCancel() {
                    colorPicker.dismissDialog()
                }
            })
                .setColumns(5)
                .setDefaultColorButton(Color.parseColor("#000000"))
                .show()
        }

        // Stroke button - toggles visibility of the RangeSlider
        stroke.setOnClickListener {
            rangeSlider.visibility = if (rangeSlider.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // Set range for the RangeSlider
        rangeSlider.valueFrom = 0.0f
        rangeSlider.valueTo = 100.0f

        // Slider to change stroke width
        rangeSlider.addOnChangeListener { slider, value, fromUser ->
            paint.setStrokeWidth(value.toInt())
        }

        // Initialize the DrawView dimensions
        val vto = paint.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                paint.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = paint.measuredWidth
                val height = paint.measuredHeight
                paint.init(height, width)
            }
        })
    }
}
