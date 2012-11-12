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

class EegRawDataVisualizationView(context: Context, attrs: AttributeSet) extends View(context, attrs) with Function1[Int, Unit] {
  val maxNumberOfPoints = 300
  var currentData: scala.collection.mutable.Queue[Int] = scala.collection.mutable.Queue[Int](maxNumberOfPoints)
  val exampleData = (1.until(100).map(x => (100 *Math.sin(x * 0.1f)).toInt))
  currentData.enqueue(exampleData: _*)
  WaveTunerPrograms.measurement.registerRawDataListener(this)
  val paint = new Paint()
  paint.setARGB(255, 255, 0, 0)
  paint.setStyle(Paint.Style.STROKE)

  override def draw(canvas: Canvas) {
    super.draw(canvas)
    val min = currentData.min
    val max = currentData.max
    val scaleY = 80.0f / (max - min)
    val scaleX = canvas.getWidth().toFloat / maxNumberOfPoints
    val scaledData = currentData.map(y => (y - min) * scaleY)
    val path = new Path()
    path.moveTo(0, scaledData(0))
    val zipped = scaledData.slice(1, currentData.size - 1).zipWithIndex
    zipped.foreach { pair => path.lineTo(pair._2 * scaleX, pair._1) }
    canvas.drawPath(path, paint)
  }
  def apply(rawData: Int) {
    currentData.enqueue(rawData)
    if (currentData.size > maxNumberOfPoints)
      currentData.dequeue
    invalidate()
  }
}