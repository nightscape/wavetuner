package org.wavetuner.programs.evaluations
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Reward
import org.wavetuner.programs.FunctionHelpers.smoothed
import org.wavetuner.programs.FunctionHelpers.temporalDevelopment
import org.wavetuner.EegChannels.bonus
import org.wavetuner.EegChannels.lowAlphaChannel
import org.wavetuner.EegChannels.lowBetaChannel
import org.wavetuner.EegChannels.thetaChannel
import scala.math.max
import scala.math.min
import org.wavetuner.R
import org.wavetuner.EegChannels
  import org.wavetuner.programs.FunctionHelpers._
  import scala.math._

class AlphaThetaEvaluation extends Evaluation {
  val shortTermSmoothingFactor = 0.9f
  val normalizedAlpha = smoothed(shortTermSmoothingFactor)
  val normalizedTheta = smoothed(shortTermSmoothingFactor)
  val normalizedBeta = smoothed(shortTermSmoothingFactor)

  val longTermSmoothingFactor = 0.99f
  val alphaDevelopmentFunction = temporalDevelopment(shortTermSmoothingFactor, longTermSmoothingFactor)
  val betaDevelopmentFunction = temporalDevelopment(shortTermSmoothingFactor, longTermSmoothingFactor)
  val thetaDevelopmentFunction = temporalDevelopment(shortTermSmoothingFactor, longTermSmoothingFactor)

  def apply(measurement: Measurement): List[Reward] = {
    val shortTermAlpha = normalizedAlpha(measurement.lowAlpha)
    val shortTermTheta = normalizedAlpha(measurement.theta)
    val shortTermBeta = normalizedAlpha(measurement.lowBeta)
    val betaDevelopment = betaDevelopmentFunction(measurement.lowBeta)
    val alphaDevelopment = alphaDevelopmentFunction(measurement.lowAlpha)
    val thetaDevelopment = thetaDevelopmentFunction(measurement.theta)
    max(min(alphaDevelopment + shortTermAlpha - 1, 1), 0)
    List(Reward(lowAlphaChannel, max(min(alphaDevelopment + shortTermAlpha - 1, 1), 0)),
    Reward(thetaChannel, max(min(thetaDevelopment + shortTermTheta - 1, 1), 0)),
    Reward(lowBetaChannel, max(min(1 - shortTermBeta, 1), 0))) ++
    (if (shortTermAlpha > 0.7 && shortTermTheta > shortTermAlpha && shortTermAlpha > shortTermBeta)
      List(Reward(bonus, 1.0f, onlyOnce = true)) else Nil)
  }

  override def toString = "Alpha-Theta"

}
