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

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] =
  {
    val optParam1 = request.getQueryString("param1")
    if (optParam1.exists{ x=> Try(x.toInt).isFailure})
      Future.successful(
              play.api.mvc.Results.Ok(views.html.error("param1 is NAN")))
    else if (optParam1.exists{ p => p.toInt<=5 &&p.toInt>=0}) block(request)
    else if (optParam1.exists{ _.toInt>5})
      Future.successful(
        play.api.mvc.Results.Ok(views.html.error("param1 is greater than 5")))
    else if (optParam1.exists{ _.toInt<0})
      Future.successful(
        play.api.mvc.Results.Ok(views.html.error("param1 is negative")))
    else Future.successful(
      play.api.mvc.Results.Ok(views.html.error("param1 is not set")))

  }

  override protected def executionContext: ExecutionContext = ec
}
