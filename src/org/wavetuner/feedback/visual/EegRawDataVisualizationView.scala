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
import android.graphics.Path
import android.scala.reactive.AndroidDomain._

class EegRawDataVisualizationView(context: Context, attrs: AttributeSet) extends View(context, attrs) with Function1[Int, Unit] with Observing {
  val maxNumberOfPoints = 500
  var currentData: scala.collection.mutable.Queue[Int] = scala.collection.mutable.Queue[Int](maxNumberOfPoints)
  var max: Int = 1
  var min: Int = -1
  val exampleData = (1.until(100).map(x => (Math.sin(x * 0.1f)).toInt))
  currentData.enqueue(exampleData: _*)
  observe(WaveTunerPrograms.measurement.rawData)(this)
  val paint = new Paint()
  paint.setARGB(255, 255, 0, 0)
  paint.setStyle(Paint.Style.STROKE)

  override def draw(canvas: Canvas) {
    super.draw(canvas)
    val scaleY = 80.0f / (max - min)
    val scaleX = canvas.getWidth().toFloat / maxNumberOfPoints
    val scaledData = currentData.map(y => (y - min) * scaleY)
    val path = new Path()
    path.moveTo(0, scaledData(0))
    val zipped = scaledData.slice(1, currentData.size - 1).zipWithIndex
    zipped.foreach { case (y, x) => path.lineTo(x * scaleX, y) }
    canvas.drawPath(path, paint)
  }
  def apply(rawData: Int) {
    if (rawData < min)
      min = rawData
    else if (rawData > max)
      max = rawData
    currentData.enqueue(rawData)
    if (currentData.size > maxNumberOfPoints)
      currentData.dequeue
    invalidate()
  }
}