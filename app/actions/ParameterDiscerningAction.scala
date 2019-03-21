package actions

import play.api.mvc._
import utils.ImplicitExtensions._

import scala.concurrent.Future
import scala.util.Try

/**
  * Created by Ilya Volynin on 27.10.2018 at 13:26.
  */
trait ParameterDiscerningAction[R[A] <: Request[A]] extends ActionBuilder[R, AnyContent] {

  override def invokeBlock[A](request: Request[A],
                              block: R[A] => Future[Result]): Future[Result] =
    validate(request, "param1")
      .respondWith(createRequest(request), block)(this.executionContext)

  def createRequest[A](request: Request[A]): R[A]

  def validate[A](request: Request[A], paramName: String): String =
    request.getQueryString(paramName)
      .fold(s"$paramName is not set")(
        parameter =>
          Try(parameter.toInt)
            .fold(_ => s"$paramName is NAN", pfLogic))

  def pfLogic: PartialFunction[Int, String]
}
