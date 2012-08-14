package org.wavetuner.eeg

case class Measurement(
  val meditation: Int = 0,
  val attention: Int = 0,
  val delta: Int = 0,
  val theta: Int = 0,
  val lowAlpha: Int = 0,
  val highAlpha: Int = 0,
  val lowBeta: Int = 0,
  val highBeta: Int = 0,
  val lowGamma: Int = 0,
  val midGamma: Int = 0)