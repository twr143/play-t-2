package actions
import play.api.mvc.Request
import scala.util.Try

/**
  * Created by Ilya Volynin on 27.10.2018 at 13:26.
  */
trait BaseAction {

  def validate[A](request: Request[A], paramName: String): String = request.getQueryString(paramName)
    .fold(s"$paramName is not set")(
      parameter =>
        Try(parameter.toInt).fold(_ => s"$paramName is NAN",
          intValue => pfLogic(intValue)))

  def pfLogic: PartialFunction[Int, String]
}
