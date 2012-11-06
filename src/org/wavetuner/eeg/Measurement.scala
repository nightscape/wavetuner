package org.wavetuner.eeg

import scala.util.Random
import com.neurosky.thinkgear.TGEegPower

object Measurement {
  def random: Measurement = new Measurement(Random.nextInt, Random.nextInt, Random.nextFloat, Random.nextFloat, Random.nextFloat, Random.nextFloat, Random.nextFloat, Random.nextFloat, Random.nextFloat, Random.nextFloat)
}

case class Measurement(
  val meditation: Int = 0,
  val attention: Int = 0,
  val delta: Float = 0,
  val theta: Float = 0,
  val lowAlpha: Float = 0,
  val highAlpha: Float = 0,
  val lowBeta: Float = 0,
  val highBeta: Float = 0,
  val lowGamma: Float = 0,
  val midGamma: Float = 0,
  val powers: TGEegPower = new TGEegPower) {
  lazy val allFrequencyPowers: Array[Float] = Array(delta, theta, lowAlpha, highAlpha, lowBeta, highBeta, lowGamma, midGamma)
  lazy val maximumFrequencyPower: Float = allFrequencyPowers.max
  lazy val allAbsolutePowers: Array[Int] = Array(powers.delta, powers.theta, powers.lowAlpha, powers.highAlpha, powers.lowBeta, powers.highBeta, powers.lowGamma, powers.midGamma)
  lazy val maximumAbsolutePower: Int = allAbsolutePowers.max
}