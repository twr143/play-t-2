package actions
import javax.inject.Inject
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import utils.ImplicitExtensions._

/**
  * Created by Ilya Volynin on 20.10.2018 at 10:21.
  */
class ValidateParamsAction @Inject()(defaultParser: BodyParsers.Default)(implicit val ec: ExecutionContext)
  extends ActionBuilder[Request, AnyContent] with BaseAction {

  override def parser: BodyParser[AnyContent] = defaultParser

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    validate(request, "param1").respondWith(request, block)
  }

  override protected def executionContext: ExecutionContext = ec

  override def pfLogic: PartialFunction[Int, String] = {
    case value if value <= 5 && value >= 0 => ""
    case value if value == 42 => "You got your answer, boss"
    case value if value > 5 => "param1 is greater than 5"
    case value => "param1 is negative"
  }
}
