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

import android.scala.reactive.AndroidDomain._

class EegVisualizationView(context: Context, attrs: AttributeSet) extends View(context, attrs) with Observing {
  var height: Int = 20
  var currentMeasurement: Measurement = Measurement.random
  observe(WaveTunerPrograms.measurement.measurements) { measurement =>
    currentMeasurement = measurement
    invalidate()
  }
  val letters = Array("δ", "θ", "α↓", "α↑", "β↓", "β↑", "γ↓", "γ→")
  val textSize = 30.0f
  val paints = letters.indices.map(paintFor(_))
  val black = new Paint()
  black.setARGB(255, 0, 0, 0)

  override def draw(canvas: Canvas) {
    super.draw(canvas)
    val barWidth = canvas.getWidth() / 8
    val barHeight = canvas.getHeight() - 20 - textSize.toInt
    for ((power, index) <- currentMeasurement.allFrequencySeries.zipWithIndex) {
      val xPosition = index * barWidth

      val rect = {
        val relative = power.currentRelativeToMaxPower
        val upper = barHeight - (relative* barHeight).toInt
        new Rect(xPosition, upper, xPosition + barWidth, barHeight);
      }
      canvas.drawRect(rect, paints(index));
      canvas.drawText(letters(index), xPosition + (barWidth - textSize * letters(index).length() / 2) / 2, barHeight + textSize, paints(index))
      val yPositionOfAverage = (1 - power.longTermRelativeToMaxPower) * barHeight
      canvas.drawRect(xPosition, yPositionOfAverage - 1, xPosition + barWidth, yPositionOfAverage + 1, black)
    }
  }
  def paintFor(index: Int): Paint = {
    val paint = new Paint();
    paint.setAntiAlias(true);
    paint.setARGB(255, 180 + index * 20, 120 - index * 10, index * 10);
    paint.setTextSize(textSize)
    paint
  }
}