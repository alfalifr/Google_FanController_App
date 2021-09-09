package com.example.android.google_fancontroller_app

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StringRes
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -35

private enum class FanSpeed(@StringRes val label: Int) {
  OFF(R.string.fan_off),
  LOW(R.string.fan_low),
  MEDIUM(R.string.fan_medium),
  HIGH(R.string.fan_high),
  ;

  fun next(): FanSpeed = when(this) {
    OFF -> LOW
    LOW -> MEDIUM
    MEDIUM -> HIGH
    HIGH -> OFF
  }
}



class DialView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
): View(
  context, attrs, defStyleAttr
) {
  //radius of circle
  private var radius = 0f
  private var fanSpeed = FanSpeed.OFF
  // position variable which will be used to draw label and indicator circle position
  private var pointPosition: PointF = PointF(0f, 0f)

  private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    textAlign = Paint.Align.CENTER
    textSize = 55f
    typeface = Typeface.create("", Typeface.BOLD)
  }

  init {
    isClickable = true
  }

  override fun onSizeChanged(
    w: Int,
    h: Int,
    oldw: Int,
    oldh: Int
  ) {
    radius = (min(h, w) / 2.0 * 0.8).toFloat()
  }

  override fun onDraw(canvas: Canvas) {
    //super.onDraw(canvas)

    //1. draw the circle background
    paint.color = if(fanSpeed == FanSpeed.OFF) Color.GRAY
    else Color.GREEN
    canvas.drawCircle(
      width.div(2).toFloat(),
      height.div(2).toFloat(),
      radius,
      paint,
    )

    //2. Draw position indicator circle
    paint.color = Color.BLACK
    val indicatorRadius = radius + RADIUS_OFFSET_INDICATOR
    pointPosition.computeXY(fanSpeed, indicatorRadius)
    canvas.drawCircle(
      pointPosition.x,
      pointPosition.y,
      radius / 12,
      paint,
    )

    //3. Draw texts / labels
    val labelRadius = radius + RADIUS_OFFSET_LABEL
    paint.color = Color.WHITE
    for(speed in FanSpeed.values()) {
      pointPosition.computeXY(speed, labelRadius)
      canvas.drawText(
        resources.getString(speed.label),
        pointPosition.x,
        pointPosition.y,
        paint,
      )
    }
  }

  override fun performClick(): Boolean {
    if(super.performClick()) return true

    fanSpeed = fanSpeed.next()
    contentDescription = resources.getString(fanSpeed.label)

    invalidate()
    return true
  }

  private fun PointF.computeXY(fanSpeed: FanSpeed, radius: Float) {
    val startAngle = Math.PI * (9 / 8.0)
    val angle = startAngle + fanSpeed.ordinal * (Math.PI / 4)
    x = (radius * cos(angle)).toFloat() + width / 2
    y = (radius * sin(angle)).toFloat() + height / 2
  }
}

