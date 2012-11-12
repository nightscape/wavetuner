package org.wavetuner.programs.evaluations
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Reward
import org.wavetuner.EegChannels.bonus
import org.wavetuner.EegChannels.standard
import org.wavetuner.programs.FunctionHelpers._

class SingleValueRewardEvaluation(val valueFunction: (Measurement => Float), val valueName: String) extends Evaluation {
  val smoother = smoothed(0.9f)

  def apply(measurement: Measurement): List[Reward] = {
    val desiredShortTermValue = valueFunction(measurement)
    val maximumOfAllValues = measurement.maximumFrequencyPower
    val desiredLongTermValue = smoother(Seq(desiredShortTermValue / maximumOfAllValues, 0).max)
    List(Reward(standard, desiredLongTermValue)) ++
      (if (desiredShortTermValue > desiredLongTermValue) List(Reward(bonus, desiredShortTermValue, onlyOnce = true)) else Nil)
  }

  override def toString = "Increase " + valueName

}
