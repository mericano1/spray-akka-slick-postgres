package app.utils

import akka.actor.ActorSystem
import akka.io.IO
import org.slf4j.{Logger, LoggerFactory}
import spray.can.Http

/**
 * Created by asalvadore on 09/05/15.
 */
trait ShutdownHook {

  private val logger: Logger = LoggerFactory.getLogger(getClass)
  implicit val system : ActorSystem

  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run() = {
      logger.info("Shutting down..")
      IO(Http) ! Http.Unbind
    }
  })

}
