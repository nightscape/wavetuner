package org.wavetuner

import _root_.android.app.Activity
import _root_.android.os.Bundle

class MainActivity extends Activity with TypedViewHolder {
  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    setContentView(R.layout.main)

    //findView(TR.textview).setText("hello, world!")
  }
}
