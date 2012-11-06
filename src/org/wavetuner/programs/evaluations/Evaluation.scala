package org.wavetuner.programs.evaluations

import org.wavetuner.eeg.Measurement
import org.wavetuner.feedback.Reward

trait Evaluation extends Function1[Measurement, List[Reward]]