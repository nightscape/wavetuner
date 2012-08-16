package org.wavetuner.programs

import org.wavetuner.eeg.MeasurementSeries
import org.wavetuner.audio.SoundPlayer
import org.wavetuner.R
import android.util.Log
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Feedback
import org.wavetuner.EegChannels

class TrainValueProgram(measurement: MeasurementSeries, feedback:Feedback, val valueFunction:(Measurement => Float), val valueName:String) extends NeuroFeedbackProgram(measurement, feedback) {
  import FunctionHelpers._
  import EegChannels._
  feedback.constantFeedbackOn(standard)
  val normalizer = normalized.andThen(smoothed(0.9f))

  def onMeasurementChange(measurement: Measurement) {
    val power = normalizer(valueFunction(measurement))
    feedback.reward(standard, power)
    if (power > 0.8)
      feedback.reward(bonus, power)
  }

  override def toString = "Train for "+valueName

}
