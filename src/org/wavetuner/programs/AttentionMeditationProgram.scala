package org.wavetuner.programs

import org.wavetuner.eeg.MeasurementSeries
import org.wavetuner.audio.SoundPlayer
import org.wavetuner.R
import android.util.Log
import org.wavetuner.eeg.Measurement

class AttentionMeditationProgram(val measurement: MeasurementSeries, soundPlayer: SoundPlayer) extends Program {
  import R.raw._
  soundPlayer.addSounds(sound_ocean, sound_unity)
  val listener: Measurement => Unit = { measurement =>
    soundPlayer.setVolume(sound_ocean, measurement.attention / 100.0f)
    soundPlayer.setVolume(sound_unity, measurement.meditation / 100.0f)
    if (measurement.attention > 30)
      soundPlayer.playOnce(sound_unity)
  }
  override def run {
    List(sound_ocean, sound_unity).foreach(soundPlayer.setVolume(_, 0.0f))
    measurement.registerMeasurementListener(listener)
    soundPlayer.playInLoop(sound_ocean, sound_unity)
  }
  def stop {
    measurement.deregisterMeasurementListener(listener)
    soundPlayer.stop
  }
  override def toString = "Attention & Meditation"

}
