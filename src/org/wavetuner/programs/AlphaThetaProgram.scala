package org.wavetuner.programs

import org.wavetuner.eeg.MeasurementSeries
import org.wavetuner.feedback.audio.SoundPlayer
import org.wavetuner.R
import android.util.Log
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Feedback
import org.wavetuner.EegChannels

class AlphaThetaProgram(measurement: MeasurementSeries, feedback: Feedback) extends NeuroFeedbackProgram(measurement, feedback) {
  import R.raw._
  import EegChannels._
  import FunctionHelpers._
  feedback.constantFeedbackOn(lowAlphaChannel, thetaChannel, lowBetaChannel)
  val smoothingFactor = 0.9f
  val normalizedAlpha = smoothed(smoothingFactor)
  val normalizedTheta = smoothed(smoothingFactor)
  val normalizedBeta = smoothed(smoothingFactor)

  def onMeasurementChange(measurement: Measurement) {
    val beta = normalizedBeta(measurement.lowBeta)
    val alpha = normalizedAlpha(measurement.lowAlpha)
    val theta = normalizedTheta(measurement.theta)
    feedback.reward(lowAlphaChannel, Seq(2 * alpha - 1, 0).max)
    feedback.reward(thetaChannel, Seq(2 * theta - 1, 0).max)
    feedback.reward(lowBetaChannel, Seq(1 - 2 * beta, 0).max)
    if (alpha > 0.7 && theta > alpha && alpha > beta)
      feedback.reward(bonus, 1.0f, onlyOnce = true)
  }

  override def toString = "Alpha-Theta"

}
