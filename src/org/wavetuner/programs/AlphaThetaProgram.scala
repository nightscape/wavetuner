package org.wavetuner.programs

import org.wavetuner.eeg.MeasurementSeries
import org.wavetuner.audio.SoundPlayer
import org.wavetuner.R
import android.util.Log
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Feedback
import org.wavetuner.EegChannels

class AlphaThetaProgram(measurement: MeasurementSeries, feedback: Feedback) extends NeuroFeedbackProgram(measurement, feedback) {
  import R.raw._
  import EegChannels._
  import FunctionHelpers._
  val normalizedAlpha = normalized.andThen(smoothed(0.9f))
  val normalizedTheta = normalized.andThen(smoothed(0.9f))
  val normalizedBeta = normalized.andThen(smoothed(0.9f))

  def onMeasurementChange(measurement: Measurement) {
    val beta = normalizedBeta(measurement.lowBeta)
    val alpha = normalizedAlpha(measurement.lowAlpha)
    val theta = normalizedTheta(measurement.theta)
    val pureAlpha = Seq(alpha-beta, 0).max
    val pureTheta = Seq(theta-beta, 0).max
    feedback.reward(lowAlphaChannel, pureAlpha)
    feedback.reward(thetaChannel,  pureTheta)
    if (pureAlpha > 0 && pureTheta > pureAlpha)
      feedback.reward(bonus, 1.0f)
  }

  override def toString = "Alpha-Theta"

}
