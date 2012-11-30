package android.scala.reactive

import scala.react.SchedulerModule
import scala.react.Domain
import android.os.Handler

object AndroidDomain extends Domain with SchedulerModule { self: Domain =>
  val handler = new Handler
  val scheduler = new ThreadSafeScheduler {
    def schedule(r: Runnable) = handler.post(r)
  }
  val engine = new Engine
  engine.runTurn
}
