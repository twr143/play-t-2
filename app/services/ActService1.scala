package services
import akka.actor.{Actor, ActorLogging}
import javax.inject.{Inject, Named}
import services.ActService1._

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by Ilya Volynin on 19.10.2018 at 15:59.
  */
class ActService1 @Inject()(@Named("showAG") showAG: Boolean,
                            @Named("aG") aG: String) extends Actor with ActorLogging {

  private implicit val ec: ExecutionContextExecutor = context.dispatcher

  override def receive: Receive = {
    case getGreeting(user) => sender() ! Greeting("Ilya from act! " + (if (showAG) aG else ""))
  }
}

object ActService1 {

  case class getGreeting(user: String)

  case class Greeting(message: String)

}
