package org.wavetuner.eeg

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
  val midGamma: Float = 0) {
  def allFrequencyPowers:Array[Float] = {
    Array(delta,theta,lowAlpha,highAlpha,lowBeta,highBeta,lowGamma,midGamma)
  }
  def maximumFrequencyPower:Float = {
    allFrequencyPowers.max
  }
}