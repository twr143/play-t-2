package actions
import play.api.mvc._

import scala.concurrent.Future
import utils.ImplicitExtensions._

import scala.collection.mutable.Set
import scala.util.{Failure, Try}

/**
  * Created by Ilya Volynin on 19.03.2019 at 13:35.
  */
trait ValidateEqualRequestsAction extends ParameterDiscerningAction[Request] {

  val processingRequestsSet: Set[String] = Set.empty
  def paramNameCheckEquals:String

  override def onCompleteCallback[A](request: Request[A]): Try[Result] => Unit
  = onCompleteCallback(request.getQueryString(paramNameCheckEquals))

  def pfLogic: PartialFunction[Int, String]
  override def validationRules: List[Rule] = List(
    Rule("param1", true, v => {
      Try(v.toInt).fold(_ => s"param1 $v is NAN", EqualRequestPFLogic)
    }),
    Rule("param2", true, v => {
      Try(v.toInt).fold(_ => s"param2 $v is NAN", pfLogic)
    })
  )

  def EqualRequestPFLogic: PartialFunction[Int, String] = {
    case i: Int if processingRequestsSet.contains(i.toString) =>
      val result = s"du[licate req. for param param1 $i"
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
//      println(s"onCompleteCallback: $processingRequestsSet")
    case Failure(ex) =>
//      println(s"onCompleteCallback Failure: ${ex.getMessage}")
  }
}