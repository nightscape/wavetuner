package org.wavetuner.feedback.visual

import android.view.View
import android.content.Context
import android.util.AttributeSet
import org.wavetuner.eeg.Measurement
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import org.wavetuner.programs.WaveTunerPrograms

class EegVisualizationView(context: Context, attrs: AttributeSet) extends View(context, attrs) with Function1[Measurement, Unit] {
  var height: Int = 20
  var currentMeasurement: Measurement = Measurement.random
  WaveTunerPrograms.measurement.registerMeasurementListener(this)

  override def draw(canvas: Canvas) {
    super.draw(canvas)

    val barWidth = canvas.getWidth() / 8
    val barHeight = canvas.getHeight()
    for ((power, index) <- currentMeasurement.allFrequencyPowers.zipWithIndex) {
      val paint = new Paint();
      paint.setAntiAlias(true);
      paint.setARGB(255, 180 + index * 20, 120 - index * 10, index * 10);
      val color = paint.getColor()
      val alpha = paint.getAlpha()
      val xPosition = index * barWidth
      val rect = new Rect(xPosition, barHeight, xPosition + barWidth, barHeight - (power * barHeight).toInt);
      canvas.drawRect(rect, paint);
    }
  }
  def apply(measurement: Measurement) {
    currentMeasurement = measurement
    invalidate()
  }
}