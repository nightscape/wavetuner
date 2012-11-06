package org.wavetuner.programs.evaluations
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Reward
import org.wavetuner.EegChannels.bonus
import org.wavetuner.EegChannels.lowAlphaChannel
import org.wavetuner.EegChannels.thetaChannel
import org.wavetuner.programs.FunctionHelpers._
import scala.math._

class SimpleAlphaThetaProgram extends Evaluation {

  def apply(measurement: Measurement): List[Reward] = {
    val alpha = measurement.powers.lowAlpha
    val theta = measurement.powers.theta
    val maxPower = measurement.maximumAbsolutePower
    val relativeAlpha = alpha.toFloat / maxPower
    val relativeTheta = theta.toFloat / maxPower
    List(Reward(lowAlphaChannel, relativeAlpha), Reward(thetaChannel, relativeTheta)) ++
      (if (relativeAlpha > 0.7 && relativeTheta > 0.7) List(Reward(bonus, 1.0f, onlyOnce = true)) else Nil)
  }

  override def toString = "Simple Alpha-Theta"

}
