package org.wavetuner.programs

import org.wavetuner.eeg.MeasurementSeries
import org.wavetuner.feedback.audio.SoundPlayer
import org.wavetuner.R
import android.util.Log
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Feedback
import org.wavetuner.EegChannels

class AttentionMeditationProgram(measurement: MeasurementSeries, feedback: Feedback) extends NeuroFeedbackProgram(measurement, feedback) {
  import R.raw._
  import FunctionHelpers._
  import EegChannels._
  feedback.constantFeedbackOn(attentionChannel, meditationChannel)

  def onMeasurementChange(measurement: Measurement) {
    val attention = measurement.attention / 100.0f
    val meditation = measurement.meditation / 100.0f
    feedback.reward(attentionChannel, attention * attention)
    feedback.reward(meditationChannel, meditation * meditation)
  }

  override def toString = "Attention & Meditation"

}
