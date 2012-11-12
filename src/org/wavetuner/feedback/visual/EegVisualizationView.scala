package org.wavetuner.feedback.visual

import android.view.View
import android.content.Context
import android.util.AttributeSet
import org.wavetuner.eeg.Measurement
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import org.wavetuner.programs.WaveTunerPrograms
import org.wavetuner.programs.FunctionHelpers

class EegVisualizationView(context: Context, attrs: AttributeSet) extends View(context, attrs) with Function1[Measurement, Unit] {
  var height: Int = 20
  var currentMeasurement: Measurement = Measurement.random
  WaveTunerPrograms.measurement.registerMeasurementListener(this)
  val letters = Array("δ", "θ", "α↓", "α↑", "β↓", "β↑", "γ↓", "γ→")
  val smoothings = letters.map { a => FunctionHelpers.smoothed(0.9f) }
  val longTermAverages = letters.map { a => FunctionHelpers.smoothed(0.95f) }
  val textSize = 30.0f
  val paints = letters.indices.map(paintFor(_))
  val black = new Paint()
  black.setARGB(255, 0, 0, 0)

  override def draw(canvas: Canvas) {
    super.draw(canvas)
    val barWidth = canvas.getWidth() / 8
    val barHeight = canvas.getHeight() - 20 - textSize.toInt
    val maxPower = currentMeasurement.maximumAbsolutePower
    for ((power, index) <- currentMeasurement.allAbsolutePowers.zipWithIndex) {
      val xPosition = index * barWidth
      val relativePower = power.toFloat / maxPower
      println("Relative power is "+relativePower)
      val rect = new Rect(xPosition, barHeight, xPosition + barWidth, barHeight - (smoothings(index)(relativePower) * barHeight).toInt);
      canvas.drawRect(rect, paints(index));
      canvas.drawText(letters(index), xPosition + (barWidth - textSize * letters(index).length() / 2) / 2, barHeight + textSize, paints(index))
      val yPositionOfAverage = (1 - longTermAverages(index)(power))*barHeight
      canvas.drawRect(xPosition, yPositionOfAverage-1, xPosition + barWidth, yPositionOfAverage+1, black)
    }
  }
  def paintFor(index: Int): Paint = {
    val paint = new Paint();
    paint.setAntiAlias(true);
    paint.setARGB(255, 180 + index * 20, 120 - index * 10, index * 10);
    paint.setTextSize(textSize)
    paint
  }
  def apply(measurement: Measurement) {
    currentMeasurement = measurement
    invalidate()
  }
}