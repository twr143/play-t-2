package controllers
import actions._
import akka.actor.ActorRef
import akka.pattern._
import akka.util.Timeout
import ch.qos.logback.core.db.dialect.MySQLDialect
import javax.inject._
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc._
import services.Greeter.{GetGreeting, Greeting}
import services.{ActorNames, NameService}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import model.Model._
import model.Model2._
import utils.ImplicitExtensions._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               nameService: NameService,
                               validateParamsAction: ValidateParamsAction,
                               sampleEqRequestAction: ValidateEqualRequestsAction,
                               validateParamsOEAction: ValidateParamsOddEvenAction[SecuredRequest],
                               @Named("showAG") showAG: Boolean,
                               @Named("aG") aG: String,
                               @Named(ActorNames.greeter) greeter: ActorRef)
                              (implicit val ec: ExecutionContext) extends AbstractController(cc) {

  implicit val timeout: Timeout = 10.seconds

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action(implicit request => Ok(views.html.index(nameService.getMyName(showAG, aG))))

  def indexAct(): Action[AnyContent] = validateParamsAction andThen validateParamsOEAction

  def eqReqAct(): Action[AnyContent] = sampleEqRequestAction { implicit request =>
    Ok(views.html.index(nameService.getMyName(showAG, aG)))
  }

  implicit def toGreeting(builder: ActionBuilder[SecuredRequest, AnyContent]): Action[AnyContent] =
    builder.async { _ =>
      (greeter ? GetGreeting("User does not matter"))
        .map {
          case Greeting(message) => Ok(views.html.index(message))
        }
    }

  def jsAct(): Action[JsValue] = Action.async(parse.tolerantJson(1024)) { req =>
    req.body.validate[AbstractPerson].fold(
      invalid => Future.successful(BadRequest(JsError.toJson(invalid))),
      person ⇒ person match {
        case p: Person => Future.successful(Ok(Json.toJson("name" -> p.name, "age" -> p.age)))
        case p: PersonL => Future.successful(Ok(Json.toJson("last" -> p.last, "age" -> p.age)))
        case p: AntiPerson => Future.successful(Ok(Json.toJson("anti" -> p.last, "age" -> p.age)))
        case p: Last => Future.successful(Ok(Json.toJson("lastonly" -> p.last)))
      }
    )
  }

  def jsActResp(): Action[JsValue] = Action.async(parse.tolerantJson(1024)) { req =>
    req.body.validate[AbstractRequest].fold(
      invalid => Future.successful(BadRequest(JsError.toJson(invalid))),
      request ⇒ request match {

        case r1: Req1 =>
          println(s"r1 works: $r1")
//          val r2 = r1.copy(first = r1.first.orElse(Some(0)))
          Future.successful(Ok(Json.toJson("requested"->Json.toJson(MyResp1(r1)))))
        case r2: Req2 =>
          println("r2 works")
          Future.successful(Ok(Json.toJson("requested"->Json.toJson(MyResp2(r2)))))
//        case r3: Req3 =>
//          println("r3 works")
//          Future.successful(Ok(Json.toJson("requested"->Json.toJson(MyResp3(r3)))))
      }
    )
  }

}
