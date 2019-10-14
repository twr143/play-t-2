package controllers

/**
  * Created by Ilya Volynin on 13.10.2019 at 20:30.
  */
import javax.inject._
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.ws
import akka.pattern.Patterns.after
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import play.api.http.websocket.TextMessage
//import ch.qos.logback.classic.Logger
import play.api.Logger
import play.api.http.websocket.Message
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import utils.DateTimeUtils

import scala.collection.immutable.Seq
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps


class WebSocketController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends AbstractController(cc) {

  implicit val timeout: Timeout = 5 seconds

  private implicit val messageFlowTransformer = MessageFlowTransformer.identityMessageFlowTransformer

  def socket: WebSocket = WebSocket.acceptOrResult[Message, Message] {
    case rh if sameOriginCheck(rh) =>
      Logger.debug(s"Incoming WS connection: ${rh.host}, ${rh.domain}, ${rh.remoteAddress}")
      Future.successful(Right(measurementsFlow))
    case rejected =>
      Logger.debug(s"Request $rejected failed same origin check")
      Future.successful(Left(Forbidden))
  }
  val measurementsFlow =
    Flow[Message].map[TextMessage](a=>a.asInstanceOf[TextMessage])
      .groupedWithin(1000, 3.second)
      .mapAsync(5)(Database.asyncBulkInsert)
      .map(written => TextMessage(s"wrote ${written.size} up to: ${written.last}, ${DateTimeUtils.currentODT}"))
  object Database {

      def asyncBulkInsert(entries: Seq[TextMessage])(implicit as: ActorSystem): Future[Seq[String]] = {
        // dispatcher for returning future might be custom
        Logger.debug(s"saved ${entries.size} messages,  [.. ${entries.last.toString} ], ${DateTimeUtils.currentODT}")
        after(30.millis, as.scheduler, as.dispatcher, Future(entries.map(_.data))(as.dispatcher))
      }
    }
  /**
    * Checks that the WebSocket comes from the same origin.  This is necessary to protect
    * against Cross-Site WebSocketHijacking as WebSocket does not implement Same Origin Policy.
    *
    * See https://tools.ietf.org/html/rfc6455#section-1.3 and
    * http://blog.dewhurstsecurity.com/2013/08/30/security-testing-html5-websockets.html
    */
  private def sameOriginCheck(rh: RequestHeader): Boolean = {
    rh.headers.get("Origin") match {
      case Some(originValue) if originMatches(originValue)=>
      Logger.debug(s"originCheck: originValue = $originValue")
        true
      case Some(badOrigin) =>
        Logger.debug(s"originCheck: rejecting request because Origin header value $badOrigin is not in the same origin")
        false
      case None =>
        Logger.debug("originCheck: rejecting request because no Origin header found")
        false
    }
  }

  private def originMatches(origin: String): Boolean = {
    //TODO: Production origin policy check
    //origins.contains(origin)
    true
  }
}
