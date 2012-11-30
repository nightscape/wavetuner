package org.wavetuner.programs

import org.wavetuner.eeg.MeasurementSeries
import org.wavetuner.feedback.audio.SoundPlayer
import org.wavetuner.R
import android.util.Log
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Feedback
import org.wavetuner.EegChannels
import org.wavetuner.programs.evaluations.Evaluation
import android.scala.reactive.AndroidDomain._

class NeuroFeedbackProgram(val evaluation: Evaluation, val measurement: MeasurementSeries, val feedback: Feedback) extends Observing {
  import R.raw._
  import EegChannels._
  import FunctionHelpers._
  import scala.math._
  def observeRunStateChanges(started: Events[_], stopped: Events[_]) {
    Reactor.loop { self =>
      self await started
      self.pause
      feedback.none
      feedback.start
      self.loopUntil(stopped) {
        onMeasurementChange(self await measurement.measurements)
        self.pause
      }
      self.pause
      feedback.stop
    }
  }

  def onMeasurementChange(measurement: Measurement) {
    for (reward <- evaluation(measurement)) {
      feedback.reward(reward)
    }
  }

  override def toString = evaluation.toString

}
