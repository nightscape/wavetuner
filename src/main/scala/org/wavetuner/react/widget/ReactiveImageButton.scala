package org.wavetuner.react.widget

import android.widget.ImageButton
import org.wavetuner.react.AndroidDomain
import org.wavetuner.react.AndroidDomain._
import android.content.Context
import android.util.AttributeSet
import android.view.View

class ReactiveImageButton(context: Context, attributeSet: AttributeSet, number: Int) extends ImageButton(context, attributeSet, number) with Observing {
  val clicks = EventSource[View]
}