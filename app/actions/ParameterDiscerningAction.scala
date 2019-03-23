package actions
import play.api.mvc._
import utils.ImplicitExtensions._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Created by Ilya Volynin on 27.10.2018 at 13:26.
  */
trait ParameterDiscerningAction[R[A] <: Request[A]] extends ActionBuilder[R, AnyContent] {

  case class Rule(paramName: String, mandatory: Boolean, validFunc: String => String)

  def validationRules: List[Rule]

  def onCompleteCallback[A](request: Request[A]): Try[Result] => Unit = { _ => () }

  def preStartEffect[A]: R[A] => R[A] = identity

  override def invokeBlock[A](request: Request[A],
                              block: R[A] => Future[Result]): Future[Result] =
    validate.andThen(process(block)(request)(this.executionContext))(request)

  // надо создавать request после всех проверок
  def createRequest[A]: Request[A] => R[A]

  def validate[A]: Request[A] => Set[String] = request =>
    validationRules.foldLeft(Set.empty[String])((currentErrors, rule) =>
      currentErrors ++ {
        request.getQueryString(rule.paramName)
          .fold(if (rule.mandatory) s"${rule.paramName} is not set" else "")(
            parameter => rule.validFunc(parameter)).toOption
      }
    )

  def process[A](block: R[A] => Future[Result])
                (request: Request[A])
                (implicit executionContext: ExecutionContext)
  : Set[String] => Future[Result] = errors => {
    if (errors.isEmpty) {
      createRequest[A].andThen(preStartEffect).andThen(block)(request)
        .andThen({ case x => onCompleteCallback(request)(x) })
    }
    else Future.successful(Results.Ok(views.html.error(errors)))
  }

}
