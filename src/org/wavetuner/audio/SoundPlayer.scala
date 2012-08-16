package org.wavetuner.audio

import android.media.SoundPool
import android.media.AudioManager
import android.content.Context
import org.wavetuner.R
import android.media.SoundPool.OnLoadCompleteListener
import android.media.MediaPlayer
import scala.collection.mutable.Map

class SoundPlayer(context: Context) {
  val mediaPlayers = Map[Int, MediaPlayer]()
  def addSounds(soundIds:Int*) {
    soundIds.foreach { soundId => mediaPlayers.put(soundId, MediaPlayer.create(context, soundId)) }
  }
  def setVolume(soundId:Int, volume:Float) {
    mediaPlayers.get(soundId).foreach(_.setVolume(volume, volume))
  }
  def setVolume(volume:Float) {
    mediaPlayers.values.foreach(_.setVolume(volume, volume))    
  }
  def playInLoop(soundIds:Int*) {
    val players = soundIds.flatMap(mediaPlayers.get(_))
    for (mediaPlayer <- players) {
      mediaPlayer.setLooping(true)
      mediaPlayer.start()
    }
  }
  def playOnce(soundId:Int) {
    mediaPlayers.get(soundId).foreach(_.start())
  }
  def stop {
    mediaPlayers.values.foreach { player =>
      player.pause
    }
    
  }
}