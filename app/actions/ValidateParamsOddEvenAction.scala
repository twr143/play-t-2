package actions

import javax.inject.Inject
import play.api.mvc._
import utils.ImplicitExtensions._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Ilya Volynin on 20.10.2018 at 11:40.
  */
abstract class ValidateParamsOddEvenAction[R[A] <: Request[A]]
(implicit val parser: BodyParsers.Default, implicit val executionContext: ExecutionContext)
  extends ActionBuilder[R, AnyContent]

final case class SecuredRequest[A](request: Request[A]) extends WrappedRequest[A](request)

class ValidateParamsOddEvenActionSecured @Inject()
(implicit val defaultParser: BodyParsers.Default, implicit val ec: ExecutionContext)
  extends ValidateParamsOddEvenAction[SecuredRequest] with BaseAction {

  override def invokeBlock[A](request: Request[A],
                              block: SecuredRequest[A] => Future[Result]): Future[Result] =
    validate(request, "param1")
      .respondWith(SecuredRequest(request), block)

  override def pfLogic: PartialFunction[Int, String] = {
    case value if value % 2 == 0 => ""
    case _ => "param1 is odd"
  }
}