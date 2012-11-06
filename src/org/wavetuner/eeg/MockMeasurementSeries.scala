package org.wavetuner.eeg;
import com.neurosky.thinkgear.TGDevice
import android.os.Handler
import android.os.Message
import scala.util.Random

class MockMeasurementSeries extends Handler with MeasurementSeries {
  sendEmptyMessageDelayed(0, 1000)
  sendEmptyMessageDelayed(1, 10)
  override def handleMessage(msg: Message) {
    msg.what match {
      case 0 => notifyMeasurementListeners(); sendEmptyMessageDelayed(0, 1000)
      case 1 => notifyRawDataListeners(); sendEmptyMessageDelayed(1, 10)
      case _ =>
    }
  }
  def currentMeasurement = Measurement.random
  def currentRawValue = Random.nextInt(10000)
}

