package actions
import javax.inject.Inject
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Created by Ilya Volynin on 20.10.2018 at 11:40.
  */
class ValidateParamsOddEvenAction @Inject()(defaultParser: BodyParsers.Default)(implicit val ec: ExecutionContext) extends ActionBuilder[Request, AnyContent] {

  override def parser: BodyParser[AnyContent] = defaultParser

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    val optParam1 = request.getQueryString("param1")
    if (optParam1.exists(_.toInt % 2 == 0)) block(request)
    else
      Future.successful(play.api.mvc.Results.Ok(views.html.error("param1 is odd")))
  }

  override protected def executionContext: ExecutionContext = ec
}