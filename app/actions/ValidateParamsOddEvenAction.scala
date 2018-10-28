package actions

import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
  * Created by Ilya Volynin on 20.10.2018 at 11:40.
  */
abstract class ValidateParamsOddEvenAction[R[A] <: Request[A]]
(implicit val parser: BodyParsers.Default, implicit val executionContext: ExecutionContext)
  extends ParameterDiscerningAction[R]

final case class SecuredRequest[A](request: Request[A]) extends WrappedRequest[A](request)

class ValidateParamsOddEvenActionSecured @Inject()
(implicit val defaultParser: BodyParsers.Default, implicit val ec: ExecutionContext)
  extends ValidateParamsOddEvenAction[SecuredRequest] {

  override def pfLogic: PartialFunction[Int, String] = {
    case value if value % 2 == 0 => ""
    case _ => "param1 is odd"
  }

  override def createRequest[A](request: Request[A]): SecuredRequest[A] = SecuredRequest(request)
}