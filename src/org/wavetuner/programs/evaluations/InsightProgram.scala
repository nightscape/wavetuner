package org.wavetuner.programs.evaluations
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Reward
import org.wavetuner.EegChannels._
import org.wavetuner.programs.FunctionHelpers._
import scala.math._

class InsightProgram extends Evaluation {
  var lastGood = 0.0f

  def apply(measurement: Measurement): List[Reward] = {
    val good = scala.math.pow(List(
      (measurement.lowAlphaMeasure.currentRelativeToHistory + measurement.thetaMeasure.currentRelativeToHistory) / 2,
      (measurement.midGammaMeasure.currentRelativeToHistory + measurement.lowGammaMeasure.currentRelativeToHistory) / 2).min,0.2f).toFloat
    val rewards = List(Reward(standard, good)) ++
      (if (measurement.deltaMeasure.longTermRelativeToHistory > 0.7)
        List(Reward(noise, 0.3f, onlyOnce = true))
      else
        Nil) ++
      (if (good > 1.5 * lastGood)
        List(Reward(bonus, 1.0f / (1.0f + scala.math.exp(lastGood - good).toFloat), onlyOnce = true))
      else
        Nil)
    lastGood = good
    rewards
  }

  override def toString = "Insight"

}
