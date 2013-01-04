package org.wavetuner

import com.xtremelabs.robolectric.RobolectricTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.wavetuner.eeg.MeasurementSeries
import org.wavetuner.programs.WaveTunerPrograms
import org.wavetuner.react.AndroidDomain
import android.widget.Button
import org.junit.Before
import android.widget.ListView

class TestMeasurement extends MeasurementSeries {
  def start {}
  def observeRawData(rawData: Int) {
    this.rawData() = rawData
    AndroidDomain.engine.runTurn
  }
}
@RunWith(classOf[RobolectricTestRunner])
class RoboElectricTest {
  import org.scalatest.matchers.ShouldMatchers._

  @Test
  def shouldHaveHappySmiles() {
    val appName = new ProgramListActivity().getResources().getString(R.string.app_name)
    assert(appName == "WaveTuner")
  }
  var activity: ProgramListActivity = null
  def programList = activity.findViewById(R.id.program_list_view).asInstanceOf[ListView]

  @Before
  def setUp() {
    activity = new ProgramListActivity();
    activity.onCreate(null);
  }

  @Test
  def shouldRecordData() {
    val measurement = new TestMeasurement
    WaveTunerPrograms.measurement = measurement
    programList.setSelection(0)
    programList.callOnClick()
    val raw = List(1, 5, 2, 4, 3)
    raw.foreach(measurement.observeRawData(_))

  }
}

