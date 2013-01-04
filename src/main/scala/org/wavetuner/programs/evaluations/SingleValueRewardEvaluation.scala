package org.wavetuner.programs.evaluations
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Reward
import org.wavetuner.EegChannels.bonus
import org.wavetuner.EegChannels.standard
import org.wavetuner.programs.FunctionHelpers._
import org.wavetuner.eeg.TimeSeries

class SingleValueRewardEvaluation(val valueFunction: (Measurement => TimeSeries), val valueName: String) extends Evaluation {

  def apply(measurement: Measurement): List[Reward] = {
    val series = valueFunction(measurement)
    val current = series.current
    val longTermNormalized = series.longTermRelativeToHistory
    val longTerm = series.longTerm
    val currentRelativeToHistory = series.currentRelativeToHistory
    List(Reward(standard, longTermNormalized)) ++
      (if (current > longTerm)
        List(Reward(bonus, currentRelativeToHistory, onlyOnce = true))
      else
        Nil)
  }

  override def toString = "Increase " + valueName

}
