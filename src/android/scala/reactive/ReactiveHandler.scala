package android.scala.reactive

import android.os.Handler
import android.os.Message
import android.scala.reactive.AndroidDomain._

object ReactiveHandler extends Handler {
  val messages: EventSource[Message] = EventSource[Message]
  override def handleMessage(msg: Message) {
    messages << msg
  }
}