package org.wavetuner.feedback

trait Feedback {
  def constantFeedbackOn(channels:Int*)
  def reward(channel:Int, reward:Float, onlyOnce:Boolean = false)
  def none
  def start
  def stop
}