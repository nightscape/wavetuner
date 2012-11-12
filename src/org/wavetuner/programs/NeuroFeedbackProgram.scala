package org.wavetuner.programs

import org.wavetuner.eeg.MeasurementSeries
import org.wavetuner.feedback.audio.SoundPlayer
import org.wavetuner.R
import android.util.Log
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Feedback
import org.wavetuner.EegChannels
import org.wavetuner.programs.evaluations.Evaluation

class NeuroFeedbackProgram(val evaluation: Evaluation, val measurement: MeasurementSeries, val feedback: Feedback) extends Runnable {
  import R.raw._
  import EegChannels._
  import FunctionHelpers._
  import scala.math._
  override def run {
    feedback.none
    measurement.registerMeasurementListener(onMeasurementChange)
    feedback.start
  }
  def stop {
    measurement.deregisterMeasurementListener(onMeasurementChange)
    feedback.stop
  }

  def onMeasurementChange(measurement: Measurement) {
    for (reward <- evaluation(measurement)) {
      feedback.reward(reward)
    }
  }

  override def toString = evaluation.toString

}
