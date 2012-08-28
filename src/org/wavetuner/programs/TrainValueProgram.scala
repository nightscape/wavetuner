package org.wavetuner.programs

import org.wavetuner.eeg.MeasurementSeries
import org.wavetuner.feedback.audio.SoundPlayer
import org.wavetuner.R
import android.util.Log
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Feedback
import org.wavetuner.EegChannels

class TrainValueProgram(val valueFunction: (Measurement => Float), val valueName: String)(implicit measurement: MeasurementSeries, feedback: Feedback) extends NeuroFeedbackProgram(measurement, feedback) {
  import FunctionHelpers._
  import EegChannels._
  feedback.constantFeedbackOn(standard)
  val smoother = smoothed(0.9f)

  def onMeasurementChange(measurement: Measurement) {
    val desiredValue = valueFunction(measurement)
    val maximumOfAllValues = measurement.maximumFrequencyPower
    val power = smoother(Seq(desiredValue / maximumOfAllValues, 0).max)
    feedback.reward(standard, power)
    if (power > 0.8)
      feedback.reward(bonus, power, onlyOnce = true)
  }

  override def toString = "Increase " + valueName

}
