package org.wavetuner.eeg

import android.scala.reactive.AndroidDomain._
import android.os.Environment
import java.io.File
import java.io._

trait Formatter[T] {
  def headers: String
  def format(value: T): String
}

object SessionRecorder {
  implicit object IntFormatter extends Formatter[Int] {
    def headers = "value"
    def format(value: Int) = value.toString
  }
  implicit object EegMeasurementFormatter extends Formatter[Measurement] {
    def headers = Measurement.valueNames.mkString("\t")
    def format(value: Measurement) = value.toList.mkString("\t")
  }
  def forRawData(observable: MeasurementSeries) = new SessionRecorder(observable.rawData, "raw")
  def forMeasurements(observable: MeasurementSeries) = new SessionRecorder(observable.measurements, "measurements")
}

class SessionRecorder[T](observable: Signal[T], sessionName: String = "")(implicit val formatter: Formatter[T]) extends Observing {
  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }
  def listen(start: Events[_], stop: Events[_]) {
    Reactor.loop { self =>
      self await start
      self.pause
      val p = new java.io.PrintWriter(logFile)
      self.loopUntil(stop) {
        p.println(formatter.format(self awaitNext observable))
      }
      p.close
      self.pause
    }
  }

  val externalStorageWriteable = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
  val logFile = new File(Environment.getExternalStorageDirectory(), sessionName + ".txt")

}