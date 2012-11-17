package org.wavetuner.eeg

import scala.util.Random
import com.neurosky.thinkgear.TGEegPower
import org.wavetuner.programs.FunctionHelpers._
import org.wavetuner.programs.SmoothingFunction
import org.wavetuner.programs.NormalizeByHistory

object Measurement {
  def random: Measurement = new Measurement(
    TimeSeries(Random.nextFloat),
    TimeSeries(Random.nextFloat),
    TimeSeries(Random.nextFloat),
    TimeSeries(Random.nextFloat),
    TimeSeries(Random.nextFloat),
    TimeSeries(Random.nextFloat),
    TimeSeries(Random.nextFloat),
    TimeSeries(Random.nextFloat),
    TimeSeries(Random.nextFloat),
    TimeSeries(Random.nextFloat), new TGEegPower)
}

class RichTGEegPower(val powers:TGEegPower) {
  lazy val allFrequencyPowers: Array[Float] =
    Array(powers.delta, powers.theta, powers.lowAlpha, powers.highAlpha, powers.lowBeta, powers.highBeta, powers.lowGamma, powers.midGamma)
  def maximumFrequencyPower = allFrequencyPowers.max
}

object EegHelpers {
  implicit def toRichTGEegPower(powers:TGEegPower) = new RichTGEegPower(powers)
}
import EegHelpers._
case class TimeSeries(
    val current: Float = 0,
    val powers: TGEegPower = new TGEegPower,
    val smoothing: SmoothingFunction = SmoothingFunction(0, 0.9f),
    val historyNormalized:NormalizeByHistory = NormalizeByHistory(0.0f,0.000001f),
    val relativePowerSmoothing: SmoothingFunction = SmoothingFunction(0,0.9f)
  ) {
  def progress(newValue: Float, powers:TGEegPower = this.powers): TimeSeries =
    TimeSeries(newValue, powers, smoothing.progress(newValue), historyNormalized.progress(newValue), relativePowerSmoothing.progress(newValue / powers.maximumFrequencyPower))
  lazy val allFrequencyPowers: Array[Float] = powers.allFrequencyPowers
  lazy val maximumFrequencyPower: Float = powers.maximumFrequencyPower
  lazy val currentRelativeToMaxPower = current / maximumFrequencyPower
  lazy val currentRelativeToHistory = historyNormalized()
  lazy val longTerm: Float = smoothing()
  lazy val longTermRelativeToMaxPower = relativePowerSmoothing()
}

case class Measurement(
  val meditationMeasure: TimeSeries = TimeSeries(),
  val attentionMeasure: TimeSeries = TimeSeries(),
  val deltaMeasure: TimeSeries = TimeSeries(),
  val thetaMeasure: TimeSeries = TimeSeries(),
  val lowAlphaMeasure: TimeSeries = TimeSeries(),
  val highAlphaMeasure: TimeSeries = TimeSeries(),
  val lowBetaMeasure: TimeSeries = TimeSeries(),
  val highBetaMeasure: TimeSeries = TimeSeries(),
  val lowGammaMeasure: TimeSeries = TimeSeries(),
  val midGammaMeasure: TimeSeries = TimeSeries(),
  val powers: TGEegPower = new TGEegPower) {
  def meditation = meditationMeasure.current
  def attention = attentionMeasure.current
  def delta = deltaMeasure.current
  def theta = thetaMeasure.current
  def lowAlpha = lowAlphaMeasure.current
  def highAlpha = highAlphaMeasure.current
  def lowBeta = lowBetaMeasure.current
  def highBeta = highBetaMeasure.current
  def lowGamma = lowGammaMeasure.current
  def midGamma = midGammaMeasure.current
  lazy val allFrequencyPowers: Array[Float] = Array(delta, theta, lowAlpha, highAlpha, lowBeta, highBeta, lowGamma, midGamma)
  lazy val maximumFrequencyPower: Float = allFrequencyPowers.max
  lazy val allAbsolutePowers: Array[Int] =
    Array(powers.delta, powers.theta, powers.lowAlpha, powers.highAlpha, powers.lowBeta, powers.highBeta, powers.lowGamma, powers.midGamma)
  lazy val maximumAbsolutePower: Int = allAbsolutePowers.max
  def progress(powers: TGEegPower = this.powers, attention: Float = this.attentionMeasure.current, meditation: Float = this.meditationMeasure.current): Measurement =
    Measurement(
      meditationMeasure.progress(meditation),
      attentionMeasure.progress(attention),
      deltaMeasure.progress(powers.delta, powers),
      thetaMeasure.progress(powers.theta, powers),
      lowAlphaMeasure.progress(powers.lowAlpha, powers),
      highAlphaMeasure.progress(powers.highAlpha, powers),
      lowBetaMeasure.progress(powers.lowBeta, powers),
      highBetaMeasure.progress(powers.highBeta, powers),
      lowGammaMeasure.progress(powers.lowGamma, powers),
      midGammaMeasure.progress(powers.midGamma, powers),
      powers)
}