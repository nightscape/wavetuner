package org.wavetuner.programs.evaluations
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Reward
import org.wavetuner.EegChannels.attentionChannel
import org.wavetuner.EegChannels.meditationChannel
import org.wavetuner.R

class AttentionMeditationEvaluation extends Evaluation {

  def apply(measurement: Measurement): List[Reward] = {
    val attention = measurement.attention
    val meditation = measurement.meditation
    List(Reward(attentionChannel, attention * attention), Reward(meditationChannel, meditation * meditation))
  }

  override def toString = "Attention & Meditation"

}
