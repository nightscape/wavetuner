package org.wavetuner.programs

object FunctionHelpers {
  def smoothed(smoothingFactor: Float) = new Function1[Float, Float] {
    var currentValue: Float = 0
    def apply(v: Float): Float = {
      currentValue = (1 - smoothingFactor) * v + smoothingFactor * currentValue
      currentValue
    }
  }
  def normalizer(maximumSmoothingFactor: Float = 1.0f) = new Function1[Float, Float] {
    var currentMaximum: Float = Float.MinPositiveValue
    def apply(v: Float): Float = {
      currentMaximum = (1 - maximumSmoothingFactor) * v + maximumSmoothingFactor * currentMaximum
      currentMaximum = Seq(v, currentMaximum).max
      v / currentMaximum
    }
  }
  def temporalDevelopment(shortTermSmoothingFactor: Float = 0.9f, longTermSmoothingFactor: Float = 0.99f) = new Function1[Float, Float] {
    val shortTermSmoothing = smoothed(shortTermSmoothingFactor)
    val longTermSmoothing = smoothed(longTermSmoothingFactor)
    def apply(v: Float): Float = {
      (shortTermSmoothing(v) + 0.000001f) / (longTermSmoothing(v) + 0.000001f)
    }

  }
  def logistic(v:Float) = {
    (1.0 / (1 + Math.exp(-v)));
  }
}