wavetuner
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

License
-------

{ placeholder for BSD or MIT license text }
