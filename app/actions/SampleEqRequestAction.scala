package actions
import javax.inject.Inject
import play.api.mvc.{AnyContent, BodyParsers, Request}

import scala.concurrent.ExecutionContext

/**
  * Created by Ilya Volynin on 19.03.2019 at 14:26.
  */
class SampleEqRequestAction @Inject()
(implicit val parser: BodyParsers.Default, val executionContext: ExecutionContext)
  extends ValidateEqualRequestsAction {

  override def pfLogic: PartialFunction[Int, String] = {
    case value if value <= 5 && value >= 0 => ""
    case value if value == 42 => "You got your answer, boss"
    case value if value > 5 => "param1 is greater than 5"
    case _ => "param1 is negative"
  }

  override def createRequest[A](request: Request[A]): Request[A] = request

}
