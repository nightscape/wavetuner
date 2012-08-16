package org.wavetuner.programs

import java.lang.Runnable
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.List
import java.util.Map
import org.wavetuner.audio.SoundPlayer
import org.wavetuner.eeg.EegMeasurementSeries
import org.wavetuner.eeg.MeasurementSeries
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Feedback
import org.wavetuner.audio.AudioFeedback
import org.wavetuner.R
import org.wavetuner.EegChannels
import org.wavetuner.audio.AudioFeedback

object WaveTunerPrograms {
  import R._
  import R.raw._
  import EegChannels._
  var programsByName: Map[String, NeuroFeedbackProgram] = _
  var programs: List[NeuroFeedbackProgram] = _
  var programNames: List[String] = _
  val measurement = EegMeasurementSeries
  def initialize(soundPlayer: SoundPlayer) {
    programsByName = programs(soundPlayer)
    programs = new ArrayList(programsByName.values)
    programNames = new ArrayList(programsByName.keySet())
  }

  def programs(soundPlayer: SoundPlayer): Map[String, NeuroFeedbackProgram] = {
    var items = new LinkedHashMap[String, NeuroFeedbackProgram]()
    val defaultSoundMap = scala.collection.immutable.Map(bonus -> sound_bell)
    val oneValueAudioFeedback = new AudioFeedback(soundPlayer, defaultSoundMap + (standard -> sound_unity))
    Seq(
      new AttentionMeditationProgram(measurement, new AudioFeedback(soundPlayer, defaultSoundMap + (lowAlphaChannel -> sound_ocean, thetaChannel -> sound_unity))),
      new AlphaThetaProgram(measurement, new AudioFeedback(soundPlayer, defaultSoundMap + (lowAlphaChannel -> sound_ocean, thetaChannel -> sound_unity))),
      new TrainValueProgram(measurement, oneValueAudioFeedback, _.meditation, "meditation"),
      new TrainValueProgram(measurement, oneValueAudioFeedback, _.attention, "attention"),
      new TrainValueProgram(measurement, oneValueAudioFeedback, _.midGamma, "mid gamma"),
      new TrainValueProgram(measurement, oneValueAudioFeedback, _.lowGamma, "low gamma"),
      new TrainValueProgram(measurement, oneValueAudioFeedback, _.highBeta, "high beta"),
      new TrainValueProgram(measurement, oneValueAudioFeedback, _.lowBeta, "low beta"),
      new TrainValueProgram(measurement, oneValueAudioFeedback, _.highAlpha, "low alpha"),
      new TrainValueProgram(measurement, oneValueAudioFeedback, _.lowAlpha, "low alpha"),
      new TrainValueProgram(measurement, oneValueAudioFeedback, _.theta, "theta"),
      new TrainValueProgram(measurement, oneValueAudioFeedback, _.delta, "delta")
    ).foreach { program =>
      items.put(program.toString, program)
    }
    return items
  }
}

abstract class NeuroFeedbackProgram(val measurement: MeasurementSeries, val feedback: Feedback) extends Runnable {
  override def run {
    feedback.none
    measurement.registerMeasurementListener(onMeasurementChange)
    feedback.start
  }
  def stop {
    measurement.deregisterMeasurementListener(onMeasurementChange)
    feedback.stop
  }
  def onMeasurementChange(measurement:Measurement)
}
