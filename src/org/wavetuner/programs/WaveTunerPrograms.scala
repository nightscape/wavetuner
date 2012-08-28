package org.wavetuner.programs

import java.lang.Runnable
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.List
import java.util.Map
import org.wavetuner.feedback.audio.SoundPlayer
import org.wavetuner.eeg.EegMeasurementSeries
import org.wavetuner.eeg.MeasurementSeries
import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Feedback
import org.wavetuner.feedback.audio.AudioFeedback
import org.wavetuner.R
import org.wavetuner.EegChannels
import org.wavetuner.eeg.MockMeasurementSeries

object WaveTunerPrograms {
  import R._
  import R.raw._
  import EegChannels._
  var programsByName: Map[String, NeuroFeedbackProgram] = _
  var programs: List[NeuroFeedbackProgram] = _
  var programNames: List[String] = _
  val measurement:MeasurementSeries = new EegMeasurementSeries
  def initialize(soundPlayer: SoundPlayer) {
    programsByName = programs(soundPlayer)
    programs = new ArrayList(programsByName.values)
    programNames = new ArrayList(programsByName.keySet())
  }

  def programs(soundPlayer: SoundPlayer): Map[String, NeuroFeedbackProgram] = {
    var items = new LinkedHashMap[String, NeuroFeedbackProgram]()
    val defaultSoundMap = scala.collection.immutable.Map(bonus -> sound_bell)
    implicit val oneValueAudioFeedback = new AudioFeedback(soundPlayer, defaultSoundMap + (standard -> sound_unity))
    implicit val theMeasurement = measurement
    Seq(
      new AttentionMeditationProgram(measurement, new AudioFeedback(soundPlayer, defaultSoundMap + (meditationChannel -> sound_ocean, attentionChannel -> sound_unity))),
      new AlphaThetaProgram(measurement, new AudioFeedback(soundPlayer, defaultSoundMap + (lowAlphaChannel -> sound_ocean, thetaChannel -> sound_unity, lowBetaChannel -> sound_brooks))),
      new TrainValueProgram(_.meditation, "Meditation"),
      new TrainValueProgram(_.attention, "Attention"),
      new TrainValueProgram(_.midGamma, "Mid Gamma"),
      new TrainValueProgram(_.lowGamma, "Low Gamma"),
      new TrainValueProgram(_.highBeta, "High Beta"),
      new TrainValueProgram(_.lowBeta, "Low Beta"),
      new TrainValueProgram(_.highAlpha, "High Alpha"),
      new TrainValueProgram(_.lowAlpha, "Low Alpha"),
      new TrainValueProgram(_.theta, "Theta"),
      new TrainValueProgram(_.delta, "Delta")).foreach { program =>
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
  def onMeasurementChange(measurement: Measurement)
}
