package org.wavetuner

import org.robolectric.RobolectricTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.wavetuner.eeg.MeasurementSeries
import org.wavetuner.programs.WaveTunerPrograms
import android.scala.reactive.AndroidDomain
import android.widget.Button
import org.junit.Before
import android.widget.ListView

@RunWith(classOf[RobolectricTestRunner])
class RoboElectricTest {
  import org.scalatest.matchers.ShouldMatchers._
	class TestMeasurement extends MeasurementSeries {
	  def startMeasuring {}
	  def observeRawData(rawData:Int) {
	    this.rawData()= rawData
	    AndroidDomain.engine.runTurn
	  }
	}
    @Test
    def shouldHaveHappySmiles()  {
        val appName = new ProgramListActivity().getResources().getString(R.string.app_name)
        appName should equal("WaveTuner")
    }
    var activity:ProgramListActivity = null
    def programList = activity.findViewById(R.id.program_list).asInstanceOf[ListView]

    @Before
    def setUp() {
        activity = new ProgramListActivity();
        activity.onCreate(null);
    }

    @Test
    def shouldRecordData()  {
      val measurement = new TestMeasurement
      WaveTunerPrograms.measurement = measurement
      programList.setSelection(0)
      programList.callOnClick()
      val raw = List(1,5,2,4,3)
      raw.foreach(measurement.observeRawData(_))
    }
}

