package org.wavetuner.audio

import org.wavetuner.feedback.Feedback
import org.wavetuner.R

class AudioFeedback(soundPlayer: SoundPlayer, channelToSound: Map[Int, Int]) extends Feedback {
  import R.raw._
  var constantFeedbackChannels: Seq[Int] = Seq()
  soundPlayer.addSounds(channelToSound.values.toArray: _*)

  def reward(channel: Int, reward: Float, onlyOnce: Boolean = false) {
    channelToSound.get(channel).foreach { soundId =>
      soundPlayer.setVolume(soundId, reward)
      if(onlyOnce) soundPlayer.playOnce(soundId)
    }
  }
  def constantFeedbackOn(channels: Int*) {
    constantFeedbackChannels = channels.toSeq
  }
  def none {
    soundPlayer.setVolume(0.0f)
  }
  def start {
    soundPlayer.playInLoop(constantFeedbackChannels.flatMap { channelToSound.get(_) }: _*)
  }
  def stop {
    soundPlayer.stop
  }
}