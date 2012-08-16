package org.wavetuner.eeg;
import com.neurosky.thinkgear.TGDevice
import android.os.Handler
import android.os.Message
import com.neurosky.thinkgear.TGEegPower
import android.os.AsyncTask
import android.view.View

trait MeasurementSeries extends Handler {
  def currentMeasurement: Measurement
  def registerMeasurementListener(listener: (Measurement => Unit))
  def deregisterMeasurementListener(listener: (Measurement => Unit))
  def registerDeviceStateChangeListener(listener: (Int => Unit))
}

object EegMeasurementSeries extends Handler with MeasurementSeries {
  import TGDevice._
  var currentMeditation = 0
  var currentAttention = 0
  var currentPowers: TGEegPower = _
  var currentDeviceState = STATE_DISCONNECTED
  var deviceStateChangeListeners = scala.collection.mutable.ArrayBuffer[(Int => Unit)]()
  var measurementListeners = scala.collection.mutable.ArrayBuffer[(Measurement => Unit)]()
  def registerDeviceStateChangeListener(listener: (Int => Unit)) {
    deviceStateChangeListeners :+= listener
  }
  def registerMeasurementListener(listener: (Measurement => Unit)) {
    measurementListeners :+= listener
  }
  def deregisterMeasurementListener(listener: (Measurement => Unit)) {
    measurementListeners -= listener
  }
  def log(l: String, s: String) { println(s) }
  def resetValues() {
    currentMeditation = 0
    currentAttention = 0
    currentPowers = new TGEegPower()
    notifyMeasurementListeners
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
      case MSG_POOR_SIGNAL => log("WaveTuner", "PoorSignal: " + msg.arg1);
      case MSG_ATTENTION => currentAttention = msg.arg1; notifyMeasurementListeners
      case MSG_MEDITATION => currentMeditation = msg.arg1; notifyMeasurementListeners
      case MSG_EEG_POWER =>
        val power = msg.obj.asInstanceOf[TGEegPower]
        currentPowers = power
        notifyMeasurementListeners
      case _ => ;
    }

  }

  def notifyMeasurementListeners {
    for (listener <- measurementListeners) {
      listener(currentMeasurement)
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

  override def currentMeasurement = new Measurement(
    meditation = currentMeditation,
    attention = currentAttention,
    delta = currentPowers.delta,
    theta = currentPowers.theta,
    lowAlpha = currentPowers.lowAlpha,
    highAlpha = currentPowers.highAlpha,
    lowBeta = currentPowers.lowBeta,
    highBeta = currentPowers.highBeta,
    lowGamma = currentPowers.lowGamma,
    midGamma = currentPowers.midGamma)
}

