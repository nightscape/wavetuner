package org.wavetuner
import _root_.android.view.View
import android.view.View.OnClickListener

trait ListenerConversions {
  implicit def function2onClickListener(f: View => Unit) = {
    new OnClickListener {
      override def onClick(view:View) {
        f(view)
      }
    }
  }
}
