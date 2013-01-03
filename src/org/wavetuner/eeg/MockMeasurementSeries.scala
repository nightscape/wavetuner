package org.wavetuner.eeg;
import com.neurosky.thinkgear.TGDevice
import com.neurosky.thinkgear.TGDevice._
import android.os.Handler
import android.os.Message
import scala.util.Random
import org.wavetuner.react.AndroidDomain

class MockMeasurementSeries extends Handler with MeasurementSeries {

  override def handleMessage(msg: Message) {
    msg.what match {
      case 0 =>
        power() = Measurement.randomPower
        meditation() = Random.nextInt(100)
        attention() = Random.nextInt(100)
        sendEmptyMessageDelayed(0, 1000)
      case 1 =>
        rawData() = Random.nextInt(100)
        sendEmptyMessageDelayed(1, 10)
      case _ =>
    }
    AndroidDomain.engine.runTurn
  }
  def start {
    sendEmptyMessageDelayed(0, 1000)
    sendEmptyMessageDelayed(1, 10)
    this.deviceStateChanges << STATE_CONNECTED
    AndroidDomain.engine.runTurn
  }
}

