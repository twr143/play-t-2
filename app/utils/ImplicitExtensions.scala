package utils
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Created by Ilya Volynin on 26.10.2018 at 14:40.
  */
object ImplicitExtensions {

  implicit class StringExt[A](sourceString: Set[String]) {

    def respondWith[T[A] <: Request[A]](request: T[A],
                                        block: T[A] => Future[Result], onCompleteCallback: Try[Result] => Unit)
                                       (implicit executionContext: ExecutionContext): Future[Result] = {
      if (sourceString.isEmpty) {
        block(request).andThen({ case x => onCompleteCallback(x) })
      }
      else Future.successful(Results.Ok(views.html.error(sourceString)))
    }
  }

}
