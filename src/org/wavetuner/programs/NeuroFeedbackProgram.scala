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
import org.wavetuner.feedback.Reward

class NeuroFeedbackProgram(val evaluation: Evaluation, val measurement: MeasurementSeries, val feedback: Feedback) extends Observing {
  import R.raw._
  import EegChannels._
  import FunctionHelpers._
  import scala.math._

  def observeRunStateChanges(started: Events[Boolean], stopped: Events[Boolean]): Reactor = {
    Reactor.loop { self =>
      self await started
      self.pause
      start
      self.loopUntil(stopped) {
        val currentRewards = self awaitNext rewards
        for (reward <- currentRewards) {
          feedback.reward(reward)
        }
      }
      stop
      self.pause
    }
  }
  val rewards = Strict[List[Reward]] {
    val currentMeasurement = measurement.measurements()
    evaluation(currentMeasurement)
  }
  def start {
    feedback.none
    feedback.start

  }

  def stop {
    feedback.stop

  }

  override def toString = evaluation.toString

}
