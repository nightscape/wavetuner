WaveTuner
=========

WaveTuner is an Android NeuroFeedback app using a Neurosky Mindwave Mobile to train your brain.
It reads the raw and processed data from the device and provides training programs that allow you to train
the amplitude of certain frequency bands or combinations thereof.

Getting started
---------------
The headset symbol in the upper left corner shows the current device connection state.
* <img src="https://raw.github.com/nightscape/wavetuner/master/res/drawable-hdpi/conn_bad.png" alt="Bad connection" height="42" width="42">
means disconnected
* <img src="https://raw.github.com/nightscape/wavetuner/master/res/drawable-hdpi/conn_fit2.png" alt="Bad connection" height="42" width="42">
means connecting 
* <img src="https://raw.github.com/nightscape/wavetuner/master/res/drawable-hdpi/conn_best.png" alt="Bad connection" height="42" width="42">
means connected

When you click on the button while the device is disconnected WaveTuner tries to connect (make sure Bluetooth is turned on).
Now choose one of the available programs. The programs give you a auditory feedback on how you're doing.
Usually it's a continuous sound that gets louder when you're doing good and a bell for an extraordinary good performance.
You can also see the raw readings of the device (the squiggly line at the bottom) and the processed amplitudes of the different frequency bands as bar graphs.

Programs
--------
* Attention & Meditation: This program has two distinct sounds for meditation ("ocean" sound) and attention ("union" sound)
* Simple Alpha-Theta: This is a rather naive implementation of the Alpha-Theta protocol which should lead you into a hypnotic state. Try to get both the ocean and the unity sound as loud as possible.
* Insight: This is similar to Alpha-Theta but has only one sound that gets louder when Alpha, Theta and Gamma (which seems to have a correspondence to intuition and sudden insights) are high.
* Increase XYZ: Trains one specific value.

For those of you that dare to read the code you can find all programs [here](https://github.com/nightscape/wavetuner/tree/master/src/main/scala/org/wavetuner/programs/evaluations).

Getting Help
------------

Have a look at the [known issues](https://github.com/nightscape/wavetuner/issues) first.
If you have a problem that is not listed here you can join the [Wavetuner group](https://groups.google.com/forum/?fromgroups#!forum/wavetuner)
and ask over there.

Build Instructions
------------------

To build Wavetuner you need Eclipse and several additional plugins:

1. Download [Eclipse 4.2](http://download.eclipse.org/eclipse/downloads/)
2. In Eclipse go to Menu -> Install new software
3. In the "Work with" text field paste the following link: http://download.scala-ide.org/sdk/e38/scala210/stable/site
4. Under "Scala IDE for Eclipse" select "Scala IDE for Eclipse", click on next and follow the installation instructions.
5. Repeat steps 2-4 with https://androidproguardscala.s3.amazonaws.com/UpdateSiteForAndroidProguardScala and "AndroidProguardScala"
6. Repeat steps 2-4 with http://dl.google.com/eclipse/plugin/4.2 and "Developer Tools" -> "Android Development Tools" and "Google plugin for Eclipse (required)" -> "Google Plugin for Eclipse 4.2"
7. Git Clone WaveTuner into your workspace
8. Import WaveTuner in Eclipse via "File" -> "Import" -> "Existing projects into workspace"
9. Now when you right click on the WaveTuner project you should be able to "Run as" -> "Android application"



License
-------

Copyright (c) <year>, <copyright holder>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the <organization> nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
