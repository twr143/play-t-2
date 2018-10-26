package actions
import actions.ValidateParamsOddEvenAction.SecuredRequest
import javax.inject.Inject
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import utils.ImplicitExtensions._

/**
  * Created by Ilya Volynin on 20.10.2018 at 11:40.
  */
abstract class ValidateParamsOddEvenAction[T[A] <: Request[A]] @Inject()(defaultParser: BodyParsers.Default)(implicit val ec: ExecutionContext) extends ActionBuilder[T, AnyContent] {

  override def parser: BodyParser[AnyContent] = defaultParser

  def createRequest[A](request: Request[A]): T[A]

  override def invokeBlock[A](request: Request[A], block: T[A] => Future[Result]): Future[Result]

  override protected def executionContext: ExecutionContext = ec
}

object ValidateParamsOddEvenAction {

  case class SecuredRequest[A](request: Request[A]) extends WrappedRequest[A](request)

}

class ValidateParamsOddEvenActionSecured @Inject()(defaultParser: BodyParsers.Default)
                                                  (override implicit val ec: ExecutionContext)
  extends ValidateParamsOddEvenAction[SecuredRequest](defaultParser)(ec) {

  override def createRequest[A](request: Request[A]): SecuredRequest[A] = SecuredRequest(request)

  override def invokeBlock[A](request: Request[A], block: SecuredRequest[A] => Future[Result]): Future[Result] = {
    requestMap(request).respondWith(SecuredRequest(request), block)
  }

  def requestMap[A](request: Request[A]): String =
    request.getQueryString("param1")
      .fold("param1 is not set")(
        parameter =>
          Try(parameter.toInt).fold(_ => "param1 is NAN",
            intValue =>
              if (intValue % 2 == 0) ""
              else "param1 is odd"
          ))
}