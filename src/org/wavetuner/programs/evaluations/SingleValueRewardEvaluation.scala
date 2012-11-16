package org.wavetuner.programs.evaluations
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Reward
import org.wavetuner.EegChannels.bonus
import org.wavetuner.EegChannels.standard
import org.wavetuner.programs.FunctionHelpers._
import org.wavetuner.eeg.TimeSeries

class SingleValueRewardEvaluation(val valueFunction: (Measurement => TimeSeries), val valueName: String) extends Evaluation {
  val smoother = smoothed(0.9f)

  def apply(measurement: Measurement): List[Reward] = {
    val series = valueFunction(measurement)
    val desiredShortTermValue = series.current
    val maximumOfAllValues = measurement.maximumFrequencyPower
    val desiredLongTermValue = series.longTerm//smoother(Seq(desiredShortTermValue / maximumOfAllValues, 0).max)
    List(Reward(standard, desiredLongTermValue)) ++
      (if (desiredShortTermValue > desiredLongTermValue) List(Reward(bonus, desiredShortTermValue, onlyOnce = true)) else Nil)
  }

  override def toString = "Increase " + valueName

}
