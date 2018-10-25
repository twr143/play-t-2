package controllers
import actions.ValidateParamsOddEvenAction.SecuredRequest
import actions.{ValidateParamsAction, ValidateParamsOddEvenAction}
import akka.actor.{ActorRef, ActorSystem}
import javax.inject._
import play.api._
import play.api.mvc._
import play.api.db.Database
import services.Service1
import akka.pattern._
import akka.stream.Materializer
import akka.util.Timeout
import services.ActService1.{Greeting, getGreeting}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               s1: Service1,
                               validateParamsAction: ValidateParamsAction,
                               validateParamsOEAction: ValidateParamsOddEvenAction[SecuredRequest],
                               @Named("showAG") showAG: Boolean,
                               @Named("aG") aG: String,
                               @Named("act1") actor1: ActorRef)(implicit system: ActorSystem, mat: Materializer, val ec: ExecutionContext) extends AbstractController(cc) {

  implicit val timeout: Timeout = 10.seconds

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(s1.getMyName(showAG, aG)))
  }

  def indexAct(): Action[AnyContent] = validateParamsAction.andThen(validateParamsOEAction).async { request: SecuredRequest[AnyContent] =>
    (actor1 ? getGreeting("")).map {
      case Greeting(message) =>
        Ok(views.html.index(message))
    }
  }
}
