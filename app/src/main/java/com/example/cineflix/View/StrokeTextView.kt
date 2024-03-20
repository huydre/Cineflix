package com.example.cineflix.View

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class StrokeTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {
    private val strokePaint: Paint = Paint()

    init {
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = 5f // set stroke width
        strokePaint.color = Color.WHITE // set stroke color
        strokePaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        val textColor = textColors.defaultColor
        setTextColor(strokePaint.color)
        paint.strokeWidth = strokePaint.strokeWidth
        paint.style = strokePaint.style
        super.onDraw(canvas)
        setTextColor(textColor)
        paint.strokeWidth = 0f
        paint.style = Paint.Style.FILL
        super.onDraw(canvas)
    }
}