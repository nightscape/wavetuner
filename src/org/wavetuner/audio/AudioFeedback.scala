package org.wavetuner.audio

import org.wavetuner.feedback.Feedback
import org.wavetuner.R

class AudioFeedback(soundPlayer: SoundPlayer, channelToSound: Map[Int, Int]) extends Feedback {
  import R.raw._
  var constantFeedbackChannels: Seq[Int] = Seq()
  soundPlayer.addSounds(channelToSound.values.toArray: _*)

  def reward(channel: Int, reward: Float) {
    channelToSound.get(channel).foreach { soundPlayer.setVolume(_, reward) }
  }
  def constantFeedbackOn(channels: Int*) {
    constantFeedbackChannels = channels.toSeq
  }
  def none {
    soundPlayer.setVolume(0.0f)
  }
  def start {
    soundPlayer.playInLoop(constantFeedbackChannels.flatMap {channelToSound.get(_)}: _*)
  }
  def stop {
    soundPlayer.stop
  }
}