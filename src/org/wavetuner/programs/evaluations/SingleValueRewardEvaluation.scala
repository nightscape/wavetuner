package org.wavetuner.programs.evaluations
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Reward
import org.wavetuner.EegChannels.bonus
import org.wavetuner.EegChannels.standard
import org.wavetuner.programs.FunctionHelpers._

class SingleValueRewardEvaluation(val valueFunction: (Measurement => Float), val valueName: String) extends Evaluation {
  val smoother = smoothed(0.9f)

  def apply(measurement: Measurement): List[Reward] = {
    val desiredValue = valueFunction(measurement)
    val maximumOfAllValues = measurement.maximumFrequencyPower
    val power = smoother(Seq(desiredValue / maximumOfAllValues, 0).max)
    List(Reward(standard, power)) ++
      (if (power > 0.8) List(Reward(bonus, power, onlyOnce = true)) else Nil)
  }

  override def toString = "Increase " + valueName

}
