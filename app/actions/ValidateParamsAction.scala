package actions

import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Created by Ilya Volynin on 20.10.2018 at 10:21.
  */
class ValidateParamsAction @Inject()(defaultParser: BodyParsers.Default)(implicit val ec: ExecutionContext) extends ActionBuilder[Request, AnyContent] {

  override def parser: BodyParser[AnyContent] = defaultParser

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    request.getQueryString("param1")
      .fold(respondWith("param1 is not set"))(
        parameter =>
          Try(parameter.toInt).fold(_ => respondWith("param1 is NAN"),
            intValue =>
              if (intValue <= 5 && intValue >= 0) block(request)
              else if (intValue == 42) respondWith("You got your answer, boss")
              else if (intValue > 5) respondWith("param1 is greater than 5")
              else respondWith("param1 is negative")))
  }

  def respondWith(text: String): Future[Result] = Future.successful(Results.Ok(views.html.error(text)))

  override protected def executionContext: ExecutionContext = ec
}
