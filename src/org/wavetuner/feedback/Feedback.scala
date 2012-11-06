package org.wavetuner.feedback

trait Feedback {
  def constantFeedbackOn(channels: Int*)
  def reward(channel: Int, reward: Float, onlyOnce: Boolean = false): Unit
  def reward(r: Reward): Unit = reward(r.channel, r.reward, r.onlyOnce)
  def none
  def start
  def stop
}

case class Reward(channel: Int, reward: Float, onlyOnce: Boolean = false)