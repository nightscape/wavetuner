package org.wavetuner.eeg;
import com.neurosky.thinkgear.TGDevice
import android.os.Handler
import android.os.Message
import scala.util.Random

class MockMeasurementSeries extends Handler {
  def registerDeviceStateChangeListener(listener: (Int => Unit)) {
  }
  override def handleMessage(msg: Message) {
  }
  def currentMeasurement = new Measurement(Random.nextInt, Random.nextInt)
}

