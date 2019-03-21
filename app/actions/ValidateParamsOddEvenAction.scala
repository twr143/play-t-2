package actions
import javax.inject.Inject
import play.api.mvc._
import scala.concurrent.ExecutionContext
import scala.util.Try

/**
  * Created by Ilya Volynin on 20.10.2018 at 11:40.
  */
trait ValidateParamsOddEvenAction[R[A] <: Request[A]] extends ParameterDiscerningAction[R]

final case class SecuredRequest[A](request: Request[A]) extends WrappedRequest[A](request)

class ValidateParamsOddEvenActionSecured @Inject()
(implicit val parser: BodyParsers.Default, val executionContext: ExecutionContext)
  extends ValidateParamsOddEvenAction[SecuredRequest] {

  def pfLogic: PartialFunction[Int, String] = {
    case value if value % 2 == 0 => ""
    case _ => "param1 is odd"
  }

  override def createRequest[A](request: Request[A]): SecuredRequest[A] = SecuredRequest(request)

  override def validationRules: List[Rule] = List(Rule("param1", true, v => {
    Try(v.toInt).fold(_ => s"$v is NAN", pfLogic)
  }))
}