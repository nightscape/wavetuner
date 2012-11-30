package org.wavetuner.eeg;
import com.neurosky.thinkgear.TGDevice
import android.os.Handler
import android.os.Message
import com.neurosky.thinkgear.TGEegPower
import android.os.AsyncTask
import android.view.View
import org.wavetuner.programs.FunctionHelpers
import scala.react.Domain
import android.app.Activity
import scala.react.EventModule
import scala.react.SchedulerModule
import android.scala.reactive.AndroidDomain
import android.scala.reactive.AndroidDomain._
import android.scala.reactive.ReactiveHandler

trait MeasurementSeries extends Handler with Observing {

  def currentMeasurement: Measurement
  def currentRawValue: Int

  var measurementListeners = scala.collection.mutable.ArrayBuffer[(Measurement => Unit)]()
  var rawDataListeners = scala.collection.mutable.ArrayBuffer[(Int => Unit)]()

  val measurements = EventSource[Measurement]

  def registerMeasurementListener(listener: (Measurement => Unit)) {
    measurementListeners :+= listener
  }
  def deregisterMeasurementListener(listener: (Measurement => Unit)) {
    measurementListeners -= listener
  }
  def notifyMeasurementListeners(measurement: Measurement = currentMeasurement) {
    measurements << measurement
    for (listener <- measurementListeners) {
      listener(measurement)
    }
  }
  def registerRawDataListener(listener: (Int => Unit)) {
    rawDataListeners :+= listener
  }
  def deregisterRawDataListener(listener: (Int => Unit)) {
    rawDataListeners -= listener
  }
  val deviceStateChanges = EventSource[Int]

  val rawData = EventSource[Int]

  def notifyRawDataListeners(rawValue: Int = currentRawValue) {
    rawData << rawValue
    for (listener <- rawDataListeners) {
      listener(rawValue)
    }
  }
}

class EegMeasurementSeries extends Handler with MeasurementSeries {
  import TGDevice._
  var currentRawValue = 0
  var currentDeviceState = STATE_DISCONNECTED
  def log(l: String, s: String) { println(s) }
  var currentMeasurement = Measurement.zero
  def resetValues() {
    currentMeasurement = Measurement.zero
    notifyMeasurementListeners(currentMeasurement)
  }
  observe(ReactiveHandler.messages)(handleTheMessage)
  def handleTheMessage(msg: Message) {
    msg.what match {
      case MSG_STATE_CHANGE =>
        currentDeviceState = msg.arg1
        deviceStateChanges << msg.arg1
        if (List(STATE_DISCONNECTED, STATE_NOT_FOUND, STATE_NOT_PAIRED, STATE_CONNECTING).contains(msg.arg1))
          resetValues()
      case MSG_ATTENTION =>
        currentMeasurement = currentMeasurement.progress(attention = msg.arg1 / 100.0f); notifyMeasurementListeners(currentMeasurement)
      case MSG_MEDITATION =>
        currentMeasurement = currentMeasurement.progress(meditation = msg.arg1 / 100.0f); notifyMeasurementListeners(currentMeasurement)
      case MSG_EEG_POWER =>
        val power = msg.obj.asInstanceOf[TGEegPower]
        currentMeasurement = currentMeasurement.progress(powers = power)
        notifyMeasurementListeners(currentMeasurement)
      case MSG_RAW_DATA =>
        currentRawValue = msg.arg1
        notifyRawDataListeners(currentRawValue)
      case _ => ;
    }

  }

  class MeasurementUpdatesAggregatorTask extends AsyncTask[Unit, Unit, Unit] {
    var measurementListenersUpdated = false
    override def doInBackground(x: Unit*) {
      Thread.sleep(200)
    }

    /**
     * The system calls this to perform work in the UI thread and delivers
     * the result from doInBackground()
     */
    override def onPostExecute(x: Unit) {
      measurementListenersUpdated = true

    }
  }
}

