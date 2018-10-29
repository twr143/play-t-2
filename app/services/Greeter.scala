package services

import akka.actor.{Actor, ActorLogging}
import javax.inject.{Inject, Named}
import services.Greeter._

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by Ilya Volynin on 19.10.2018 at 15:59.
  */
class Greeter @Inject()(@Named("showAG") showAG: Boolean,
                        @Named("aG") aG: String) extends Actor with ActorLogging {

  private implicit val ec: ExecutionContextExecutor = context.dispatcher

  override def receive: Receive = {
    case GetGreeting(_) =>
      sender() ! Greeting("Ilya, greeting from Actor Greeter! " + (if (showAG) aG else ""))
  }
}

object Greeter {

  case class GetGreeting(user: String)

  case class Greeting(message: String)

}
