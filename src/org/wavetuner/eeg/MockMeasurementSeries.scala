package org.wavetuner.eeg;
import com.neurosky.thinkgear.TGDevice
import android.os.Handler
import android.os.Message
import scala.util.Random

class MockMeasurementSeries extends Handler with MeasurementSeries {
  override def handleMessage(msg: Message) {
    notifyMeasurementListeners
  }
  def currentMeasurement = new Measurement(Random.nextInt, Random.nextInt, Random.nextFloat, Random.nextFloat, Random.nextFloat, Random.nextFloat, Random.nextFloat, Random.nextFloat)
}

