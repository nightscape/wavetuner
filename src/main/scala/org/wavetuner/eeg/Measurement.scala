package org.wavetuner.eeg

import scala.util.Random
import com.neurosky.thinkgear.TGEegPower
import org.wavetuner.programs.FunctionHelpers._
import org.wavetuner.programs.SmoothingFunction
import org.wavetuner.programs.NormalizeByHistory
import EegHelpers._
import scala.collection.mutable.LinkedHashMap
import scala.collection.immutable.ListMap
import org.wavetuner.programs.Maximum

object Measurement {
  def randomPower: TGEegPower = new TGEegPower(
    Random.nextInt(50000),
    Random.nextInt(50000),
    Random.nextInt(50000),
    Random.nextInt(50000),
    Random.nextInt(50000),
    Random.nextInt(50000),
    Random.nextInt(50000),
    Random.nextInt(50000))
  def zero: Measurement = new Measurement(
    TimeSeries(0.0f),
    TimeSeries(0.0f),
    TimeSeries(0.0f),
    TimeSeries(0.0f),
    TimeSeries(0.0f),
    TimeSeries(0.0f),
    TimeSeries(0.0f),
    TimeSeries(0.0f),
    TimeSeries(0.0f),
    TimeSeries(0.0f), new TGEegPower)
  def random = zero.progress(randomPower, Random.nextFloat, Random.nextFloat)
  val valueExtractors: Map[String, Measurement => Float] = ListMap(
    "meditation" -> { m: Measurement => m.meditation },
    "attention" -> { m: Measurement => m.attention },
    "delta" -> { m: Measurement => m.delta },
    "theta" -> { m: Measurement => m.theta },
    "lowAlpha" -> { m: Measurement => m.lowAlpha },
    "highAlpha" -> { m: Measurement => m.highAlpha },
    "lowBeta" -> { m: Measurement => m.lowBeta },
    "highBeta" -> { m: Measurement => m.highBeta },
    "lowGamma" -> { m: Measurement => m.lowGamma },
    "midGamma" -> { m: Measurement => m.midGamma })

  val valueNames = valueExtractors.keys
  val valueExtensions = List(
    "longTerm", "relativeToMaxPower", "relativeToHistory", "longTermRelativeToMaxPower")
}

class RichTGEegPower(val powers: TGEegPower) {
  lazy val allFrequencyPowers: Array[Float] =
    Array(powers.delta, powers.theta, powers.lowAlpha, powers.highAlpha, powers.lowBeta, powers.highBeta, powers.lowGamma, powers.midGamma)
  def maximumFrequencyPower = allFrequencyPowers.max
}

object EegHelpers {
  implicit def toRichTGEegPower(powers: TGEegPower) = new RichTGEegPower(powers)
}
case class TimeSeries(
  val current: Float = 0,
  val powers: TGEegPower = new TGEegPower,
  val smoothing: SmoothingFunction = SmoothingFunction(0, 0.9f),
  val historicMaximum: Maximum = new Maximum(0.000001f),
  val relativePowerSmoothing: SmoothingFunction = SmoothingFunction(0, 0.9f)) {
  def progress(newValue: Float, powers: TGEegPower = this.powers): TimeSeries =
    TimeSeries(newValue, powers, smoothing.progress(newValue), historicMaximum.progress(newValue), relativePowerSmoothing.progress(newValue / scala.math.max(powers.maximumFrequencyPower, 1.0f)))
  lazy val allFrequencyPowers: Array[Float] = powers.allFrequencyPowers
  lazy val maximumFrequencyPower: Float = powers.maximumFrequencyPower
  lazy val currentRelativeToMaxPower = current / maximumFrequencyPower
  lazy val currentRelativeToHistory = current / historicMaximum()
  lazy val longTerm: Float = smoothing()
  lazy val longTermRelativeToHistory = smoothing() / historicMaximum()
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
  lazy val allFrequencySeries: Array[TimeSeries] = Array(deltaMeasure, thetaMeasure, lowAlphaMeasure, highAlphaMeasure,
    lowBetaMeasure, highBetaMeasure, lowGammaMeasure, midGammaMeasure)
  lazy val allFrequencyPowers: Array[Float] = Array(delta, theta, lowAlpha, highAlpha, lowBeta, highBeta, lowGamma, midGamma)
  lazy val maximumFrequencyPower: Float = allFrequencyPowers.max
  lazy val allAbsolutePowers: Array[Float] = powers.allFrequencyPowers
  lazy val maximumAbsolutePower: Float = powers.maximumFrequencyPower
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
  def toList: List[Float] = Measurement.valueExtractors.values.map(e => e(this)).toList
}