package org.wavetuner.eeg;
import com.neurosky.thinkgear.TGDevice
import android.os.Handler
import android.os.Message
import scala.util.Random

class MockMeasurementSeries extends Handler with MeasurementSeries {
  sendEmptyMessageDelayed(0, 1000)
  sendEmptyMessageDelayed(1, 10)
  override def handleMessage(msg: Message) {
    currentMeasurement = currentMeasurement.progress(Measurement.randomPower, Random.nextFloat, Random.nextFloat)
    msg.what match {
      case 0 => notifyMeasurementListeners(currentMeasurement); sendEmptyMessageDelayed(0, 1000)
      case 1 => notifyRawDataListeners(currentRawValue); sendEmptyMessageDelayed(1, 10)
      case _ =>
    }
  }
  var currentMeasurement = Measurement.random
  def currentRawValue = Random.nextInt(10000)
}

