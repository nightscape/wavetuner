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

object AndroidDomain extends Domain with SchedulerModule { self: Domain =>
  val handler = new Handler
  val scheduler = new ThreadSafeScheduler {
    def schedule(r: Runnable) = handler.post(r)
  }
  val engine = new Engine
  engine.runTurn
}

trait MeasurementSeries extends Handler {
  import AndroidDomain._

  def currentMeasurement: Measurement
  def currentRawValue: Int

  var deviceStateChangeListeners = scala.collection.mutable.ArrayBuffer[(Int => Unit)]()
  var measurementListeners = scala.collection.mutable.ArrayBuffer[(Measurement => Unit)]()
  var rawDataListeners = scala.collection.mutable.ArrayBuffer[(Int => Unit)]()

  val measurements: EventSource[Measurement] = new EventSource[Measurement](AndroidDomain.owner) { self =>
    registerMeasurementListener { measurement =>
      self emit measurement
    }
  }
  def registerDeviceStateChangeListener(listener: (Int => Unit)) {
    deviceStateChangeListeners :+= listener
  }
  def registerMeasurementListener(listener: (Measurement => Unit)) {
    measurementListeners :+= listener
  }
  def deregisterMeasurementListener(listener: (Measurement => Unit)) {
    measurementListeners -= listener
  }
  def notifyMeasurementListeners(measurement: Measurement = currentMeasurement) {
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
  def notifyRawDataListeners(rawValue: Int = currentRawValue) {
    for (listener <- rawDataListeners) {
      listener(rawValue)
    }
  }
}

class EegMeasurementSeries extends Handler with MeasurementSeries {
  import TGDevice._
  var currentMeditation = 0
  var currentAttention = 0
  var currentPowers: TGEegPower = _
  var currentRawValue = 0
  var currentDeviceState = STATE_DISCONNECTED
  def log(l: String, s: String) { println(s) }
  def resetValues() {
    currentMeditation = 0
    currentAttention = 0
    currentPowers = new TGEegPower()
    notifyMeasurementListeners()
  }
  override def handleMessage(msg: Message) {
    msg.what match {
      case MSG_STATE_CHANGE =>
        currentDeviceState = msg.arg1
        for (listener <- deviceStateChangeListeners) {
          listener(msg.arg1)
        }
        if (List(STATE_DISCONNECTED, STATE_NOT_FOUND, STATE_NOT_PAIRED, STATE_CONNECTING).contains(msg.arg1))
          resetValues()
      case MSG_ATTENTION =>
        currentAttention = msg.arg1; notifyMeasurementListeners()
      case MSG_MEDITATION =>
        currentMeditation = msg.arg1; notifyMeasurementListeners()
      case MSG_EEG_POWER =>
        val power = msg.obj.asInstanceOf[TGEegPower]
        currentPowers = power
        notifyMeasurementListeners()
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
  import FunctionHelpers._
  val smoothingFactor = 0.9f
  val deltaNormalizer = normalizer(smoothingFactor)
  val thetaNormalizer = normalizer(smoothingFactor)
  val lowAlphaNormalizer = normalizer(smoothingFactor)
  val highAlphaNormalizer = normalizer(smoothingFactor)
  val lowBetaNormalizer = normalizer(smoothingFactor)
  val highBetaNormalizer = normalizer(smoothingFactor)
  val lowGammaNormalizer = normalizer(smoothingFactor)
  val midGammaNormalizer = normalizer(smoothingFactor)
  override def currentMeasurement = new Measurement(
    meditation = currentMeditation,
    attention = currentAttention,
    delta = deltaNormalizer(currentPowers.delta),
    theta = thetaNormalizer(currentPowers.theta),
    lowAlpha = lowAlphaNormalizer(currentPowers.lowAlpha),
    highAlpha = highAlphaNormalizer(currentPowers.highAlpha),
    lowBeta = lowBetaNormalizer(currentPowers.lowBeta),
    highBeta = highBetaNormalizer(currentPowers.highBeta),
    lowGamma = lowGammaNormalizer(currentPowers.lowGamma),
    midGamma = midGammaNormalizer(currentPowers.midGamma),
    powers = currentPowers)
}

