package org.wavetuner.programs

import java.lang.Runnable
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.{ List => JList }
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
import org.wavetuner.programs.evaluations.AlphaThetaEvaluation
import org.wavetuner.programs.evaluations.AttentionMeditationEvaluation
import org.wavetuner.programs.evaluations.SimpleAlphaThetaProgram
import org.wavetuner.programs.evaluations.SingleValueRewardEvaluation
import org.wavetuner.programs.evaluations.Evaluation
import scala.collection.JavaConversions._

object WaveTunerPrograms {
  import R._
  import R.raw._
  import EegChannels._
  var programsByName: Map[String, NeuroFeedbackProgram] = _
  var programs: JList[NeuroFeedbackProgram] = _
  var programNames: JList[String] = _
  var measurement: MeasurementSeries = new EegMeasurementSeries
  def initialize(soundPlayer: SoundPlayer) {
    programsByName = programs(soundPlayer)
    programs = new ArrayList(programsByName.values)
    programNames = new ArrayList(programsByName.keySet())
  }

  def programs(soundPlayer: SoundPlayer): Map[String, NeuroFeedbackProgram] = {
    val defaultSoundMap = scala.collection.immutable.Map(bonus -> sound_bell)
    val oneValueAudioFeedback = new AudioFeedback(soundPlayer, defaultSoundMap + (standard -> sound_unity))
    oneValueAudioFeedback.constantFeedbackChannels(standard)
    val programs = List(
      new NeuroFeedbackProgram(new AttentionMeditationEvaluation, measurement, new AudioFeedback(soundPlayer, defaultSoundMap + (meditationChannel -> sound_ocean, attentionChannel -> sound_unity), attentionChannel, meditationChannel)),
      new NeuroFeedbackProgram(new AlphaThetaEvaluation, measurement, new AudioFeedback(soundPlayer, defaultSoundMap + (lowAlphaChannel -> sound_ocean, thetaChannel -> sound_unity, lowBetaChannel -> sound_brooks), lowAlphaChannel, thetaChannel, lowBetaChannel)),
      new NeuroFeedbackProgram(new SimpleAlphaThetaProgram, measurement, new AudioFeedback(soundPlayer, defaultSoundMap + (lowAlphaChannel -> sound_ocean, thetaChannel -> sound_unity, lowBetaChannel -> sound_brooks), lowAlphaChannel, thetaChannel, lowBetaChannel))) ++ List(
        new SingleValueRewardEvaluation(_.meditation, "Meditation"),
        new SingleValueRewardEvaluation(_.attention, "Attention"),
        new SingleValueRewardEvaluation(_.midGamma, "Mid Gamma"),
        new SingleValueRewardEvaluation(_.lowGamma, "Low Gamma"),
        new SingleValueRewardEvaluation(_.highBeta, "High Beta"),
        new SingleValueRewardEvaluation(_.lowBeta, "Low Beta"),
        new SingleValueRewardEvaluation(_.highAlpha, "High Alpha"),
        new SingleValueRewardEvaluation(_.lowAlpha, "Low Alpha"),
        new SingleValueRewardEvaluation(_.theta, "Theta"),
        new SingleValueRewardEvaluation(_.delta, "Delta")).map(evaluation => new NeuroFeedbackProgram(evaluation, measurement, oneValueAudioFeedback))
    new LinkedHashMap[String, NeuroFeedbackProgram](programs.map(p => (p.toString, p)).toMap)
  }
}
