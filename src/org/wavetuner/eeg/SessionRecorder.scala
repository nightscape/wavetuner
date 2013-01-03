package org.wavetuner.eeg

import org.wavetuner.react.AndroidDomain._
import android.os.Environment
import java.io.File
import java.io._
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import android.util.Log

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
  val dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss")
  def listen(start: Events[_], stop: Events[_]) {
    Reactor.loop { self =>
      self await start
      val logFile = new File(path, sessionName + "_" + dateFormatter.format(new Date()) + ".txt")
      val p = new java.io.PrintWriter(logFile)
      Log.i("WaveTuner Session Recorder", "Recording " + sessionName + " data to file " + logFile)
      p.println(formatter.headers)
      self.pause
      self.loopUntil(stop) {
        p.println(formatter.format(self await observable))
        Log.i("WaveTuner Session Recorder", "Recorded value")
        self.pause
      }
      p.close
      Log.i("WaveTuner Session Recorder", "Closed recorded session file " + logFile)
      self.pause
    }
  }
  val path = new File(Environment.getExternalStorageDirectory(), "wavetuner")
  path.mkdirs()
  val externalStorageWriteable = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())

}