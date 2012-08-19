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
}