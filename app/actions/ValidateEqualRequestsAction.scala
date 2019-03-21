package actions
import play.api.mvc._
import scala.concurrent.Future
import utils.ImplicitExtensions._
import scala.collection.mutable.Set
import scala.util.Try

/**
  * Created by Ilya Volynin on 19.03.2019 at 13:35.
  */
trait ValidateEqualRequestsAction extends ParameterDiscerningAction[Request] {

  val processingRequestsSet: Set[String] = Set.empty
  val paramName = "param2"
  val validationRules: List[(String, PartialFunction[Int, Set[String]])] =
    List(("param1", {
      case value if value <= 5 && value >= 0 => Set.empty
      case value if value == 42 => Set("param1 You got your answer, boss")
      case value if value > 5 => Set("param1 is greater than 5")
      case _ => Set("param1 is negative")
    }), ("param2", {
      case value if value <= 5 && value >= 0 => Set.empty
      case value if value == 42 => Set("param2 You got your answer, boss")
      case value if value > 5 => Set("param2 is greater than 5")
      case _ => Set("param2 is negative")
    }))

  override def invokeBlock[A](request: Request[A],
                              block: Request[A] => Future[Result]): Future[Result] =
    validate(request, paramName)
      .respondWith(createRequest(request),
        block,
        onCompleteCallback(request.getQueryString(paramName)))(this.executionContext)

  override def validate[A](request: Request[A], paramName: String): String =
    request.getQueryString(paramName)
      .fold(s"$paramName is not set")(
        parameter =>
          Try(parameter.toInt)
            .fold(_ => s"$paramName is NAN", EqualRequestPFLogic))

  def EqualRequestPFLogic: PartialFunction[Int, String] = {
    case i: Int if processingRequestsSet.contains(i.toString) =>
      val result = s"du[licate req. for param param2 $i"
      println(result)
      result
    case i: Int  =>
      val checkResult = pfLogic(i)
      if (checkResult.isEmpty)
        processingRequestsSet += (i.toString)
      println(s"procResultSet: $processingRequestsSet")
      checkResult
  }

  def onCompleteCallback(parameterValue: Option[String]):
  Try[Result] => Unit = {
    case scala.util.Success(_) =>
      parameterValue.map(processingRequestsSet -= _)
  }
}