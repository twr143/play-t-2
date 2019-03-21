package actions

import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.util.Try

/**
  * Created by Ilya Volynin on 20.10.2018 at 10:21.
  */
class ValidateParamsAction @Inject()
(implicit val parser: BodyParsers.Default, val executionContext: ExecutionContext)
  extends ParameterDiscerningAction[Request] {

  def pfLogic: PartialFunction[Int, String] = {
    case value if value <= 5 && value >= 0 => ""
    case value if value == 42 => "You got your answer, boss"
    case value if value > 5 => "param1 is greater than 5"
    case _ => "param1 is negative"
  }

  override def createRequest[A](request: Request[A]): Request[A] = request

  override def validationRules: List[Rule] = List(Rule("param1",true, v =>{
    Try(v.toInt).fold(_=>s"param1 $v is NAN", pfLogic)
  }))
}
