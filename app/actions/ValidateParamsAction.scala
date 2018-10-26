package actions
import javax.inject.Inject
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import utils.ImplicitExtensions._

/**
  * Created by Ilya Volynin on 20.10.2018 at 10:21.
  */
class ValidateParamsAction @Inject()(defaultParser: BodyParsers.Default)(implicit val ec: ExecutionContext) extends ActionBuilder[Request, AnyContent] {

  override def parser: BodyParser[AnyContent] = defaultParser

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    requestMap(request).respondWith(request, block)
  }

  def requestMap[A](request: Request[A]): String =
    request.getQueryString("param1")
      .fold("param1 is not set")(
        parameter =>
          Try(parameter.toInt).fold(_ => "param1 is NAN",
            intValue =>
              if (intValue <= 5 && intValue >= 0) ""
              else if (intValue == 42) "You got your answer, boss"
              else if (intValue > 5) "param1 is greater than 5"
              else "param1 is negative"))

  override protected def executionContext: ExecutionContext = ec
}
