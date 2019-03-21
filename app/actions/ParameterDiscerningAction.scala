package actions
import play.api.mvc._
import utils.ImplicitExtensions._
import scala.concurrent.Future
import scala.util.Try

/**
  * Created by Ilya Volynin on 27.10.2018 at 13:26.
  */
trait ParameterDiscerningAction[R[A] <: Request[A]] extends ActionBuilder[R, AnyContent] {

  case class Rule(paramName: String, mandatory: Boolean, validFunc: String => String)

  def validationRules: List[Rule]

  def onCompleteCallback[A](request: Request[A]): Try[Result] => Unit = { _ => () }

  override def invokeBlock[A](request: Request[A],
                              block: R[A] => Future[Result]): Future[Result] =
    validate(request)
      .respondWith(createRequest(request), block, onCompleteCallback(request))(this.executionContext)

  def createRequest[A](request: Request[A]): R[A]

  def validate[A](request: Request[A]) =
    validationRules.foldLeft(Set.empty[String])((currentErrors: Set[String], rule: Rule) =>
      currentErrors ++ {
        val result = request.getQueryString(rule.paramName)
          .fold(if (rule.mandatory) s"${rule.paramName} is not set" else "")(
            parameter => rule.validFunc(parameter))
        if (result.isEmpty) None else Some(result)
      }
    )
}
