package org.wavetuner.eeg

import scala.util.Random
import com.neurosky.thinkgear.TGEegPower
import org.wavetuner.programs.FunctionHelpers._
import org.wavetuner.programs.SmoothingFunction

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
    TimeSeries(Random.nextFloat))
}

case class TimeSeries(val current: Float = 0, val smoothing: SmoothingFunction = SmoothingFunction(0, 0.9f)) {
  def progress(newValue: Float): TimeSeries = TimeSeries(newValue, smoothing.progress(newValue))
  def longTerm: Float = smoothing()

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
  lazy val allAbsolutePowers: Array[Int] = Array(powers.delta, powers.theta, powers.lowAlpha, powers.highAlpha, powers.lowBeta, powers.highBeta, powers.lowGamma, powers.midGamma)
  lazy val maximumAbsolutePower: Int = allAbsolutePowers.max
  def progress(powers: TGEegPower = this.powers, attention: Float = this.attentionMeasure.current, meditation: Float = this.meditationMeasure.current): Measurement =
    Measurement(
      meditationMeasure.progress(meditation),
      attentionMeasure.progress(attention),
      deltaMeasure.progress(powers.delta),
      thetaMeasure.progress(powers.theta),
      lowAlphaMeasure.progress(powers.lowAlpha),
      highAlphaMeasure.progress(powers.highAlpha),
      lowBetaMeasure.progress(powers.lowBeta),
      highBetaMeasure.progress(powers.highBeta),
      lowGammaMeasure.progress(powers.lowGamma),
      midGammaMeasure.progress(powers.midGamma),
      powers)
}