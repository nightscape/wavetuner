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
import org.wavetuner.react.AndroidDomain
import org.wavetuner.react.AndroidDomain._
import org.wavetuner.react.ReactiveHandler
import android.bluetooth.BluetoothAdapter
import scala.collection.mutable.Queue

trait MeasurementSeries extends Handler with Observing {

  val attention = Var[Int](0)
  val meditation = Var[Int](0)
  val power = Var[TGEegPower](new TGEegPower)
  val measurements: Signal[Measurement] = Signal.flow(Measurement.zero) { self =>
    val rawDataQueue = rawSequence.now
    val rawDataSequence = rawDataQueue.toArray
    rawDataQueue.clear
    self() = self.previous.progress(power(), attention(), meditation(), rawDataSequence)
  }

  val deviceStateChanges = EventSource[Int]

  val rawData = Var[Int](0)
  val rawSequence: Signal[Queue[Int]] = Signal.flow(Queue[Int]()) { self =>
    self.previous.enqueue(rawData())
    self() = self.previous
  }

  def start
}

class EegMeasurementSeries extends Handler with MeasurementSeries {
  import TGDevice._

  observe(ReactiveHandler.messages)(handleTheMessage)
  def handleTheMessage(msg: Message) {
    msg.what match {
      case MSG_STATE_CHANGE =>
        deviceStateChanges << msg.arg1
        if (List(STATE_DISCONNECTED, STATE_NOT_FOUND, STATE_NOT_PAIRED, STATE_CONNECTING).contains(msg.arg1))
          println("SHOULD RESET HERE")
      case MSG_ATTENTION =>
        attention() = msg.arg1
      case MSG_MEDITATION =>
        meditation() = msg.arg1
      case MSG_EEG_POWER =>
        power() = msg.obj.asInstanceOf[TGEegPower]
      case MSG_RAW_DATA =>
        rawData() = msg.arg1
      case _ => ;
    }

  }
  def start() {
    val btAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    if (btAdapter != null) {
      val tgDevice = new TGDevice(btAdapter, ReactiveHandler)
      observe(this.deviceStateChanges)(status => if (status == TGDevice.STATE_CONNECTED) tgDevice.start)
      tgDevice.connect(true)
    }
  }
}

