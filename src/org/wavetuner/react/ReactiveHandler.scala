package org.wavetuner.react

import android.os.Handler
import android.os.Message
import org.wavetuner.react.AndroidDomain._

object ReactiveHandler extends Handler {
  val messages: EventSource[Message] = EventSource[Message]
  override def handleMessage(msg: Message) {
    messages << msg
    AndroidDomain.engine.runTurn
  }
}