package org.wavetuner.programs

import java.lang.Runnable
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.List
import java.util.Map

import org.wavetuner.audio.SoundPlayer
import org.wavetuner.eeg.EegMeasurementSeries
import org.wavetuner.eeg.MeasurementSeries

object WaveTunerPrograms {

  var programsByName: Map[String, Program] = _
  var programs: List[Program] = _
  var programNames: List[String] = _
  val measurement = EegMeasurementSeries
  def initialize(soundPlayer: SoundPlayer) {
    programsByName = programs(soundPlayer)
    programs = new ArrayList(programsByName.values)
    programNames = new ArrayList(programsByName.keySet())
  }

  def programs(soundPlayer: SoundPlayer): Map[String, Program] = {
    var items = new LinkedHashMap[String, Program]()
    scala.collection.immutable.List(new AttentionMeditationProgram(measurement, soundPlayer)).foreach { program =>
      items.put(program.toString, program)
    }
    return items
  }
}

trait Program extends Runnable {
  var isRunning = false
  def run {
    isRunning = true
  }
  def stop
}
