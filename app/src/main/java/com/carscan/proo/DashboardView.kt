package com.carscan.proo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class DashboardView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var speed = 0f
    var temperature = 0f
    var rpm = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f

        drawGauge(
            canvas,
            centerX,
            centerY - 180f,
            speed,
            240f,
            "SPEED",
            "km/h"
        )

        drawGauge(
            canvas,
            centerX - 130f,
            centerY + 120f,
            temperature,
            140f,
            "TEMP",
            "°C"
        )

        drawGauge(
            canvas,
            centerX + 130f,
            centerY + 120f,
            rpm,
            8000f,
            "RPM",
            "rpm"
        )
    }

    private fun drawGauge(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        value: Float,
        max: Float,
        title: String,
        unit: String
    ) {
        paint.color = Color.DKGRAY
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 16f

        val rect = RectF(
            cx - 90f,
            cy - 90f,
            cx + 90f,
            cy + 90f
        )

        canvas.drawArc(rect, 135f, 270f, false, paint)

        paint.color = Color.CYAN

        val sweep = (value / max).coerceIn(0f, 1f) * 270f

        canvas.drawArc(rect, 135f, sweep, false, paint)

        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 30f

        canvas.drawText(
            value.toInt().toString(),
            cx,
            cy + 10f,
            paint
        )

        paint.textSize = 17f

        canvas.drawText(
            title,
            cx,
            cy + 38f,
            paint
        )

        paint.textSize = 13f

        canvas.drawText(
            unit,
            cx,
            cy + 57f,
            paint
        )
    }

    fun updateData(
        newSpeed: Float,
        newTemperature: Float,
        newRpm: Float
    ) {
        speed = newSpeed
        temperature = newTemperature
        rpm = newRpm
        invalidate()
    }
}
