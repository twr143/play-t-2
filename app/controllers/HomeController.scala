package controllers

import actions.{SecuredRequest, ValidateParamsAction, ValidateParamsOddEvenAction}
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern._
import akka.stream.Materializer
import akka.util.Timeout
import javax.inject._
import play.api.mvc._
import services.Greeter.{GetGreeting, Greeting}
import services.{ActorNames, NameService}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               nameService: NameService,
                               validateParamsAction: ValidateParamsAction,
                               validateParamsOEAction: ValidateParamsOddEvenAction[SecuredRequest],
                               @Named("showAG") showAG: Boolean,
                               @Named("aG") aG: String,
                               @Named(ActorNames.greeter) greeter: ActorRef)
                              (implicit system: ActorSystem,
                               mat: Materializer,
                               val ec: ExecutionContext) extends AbstractController(cc) {

  implicit val timeout: Timeout = 10.seconds

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action(implicit request => Ok(views.html.index(nameService.getMyName(showAG, aG))))

  def indexAct(): Action[AnyContent] =
    validateParamsAction.andThen(validateParamsOEAction)
      .async { _ =>
        (greeter ? GetGreeting("User does not matter"))
          .map {
            case Greeting(message) => Ok(views.html.index(message))
          }
      }
}
