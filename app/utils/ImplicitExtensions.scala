package utils
import play.api.mvc._
import scala.concurrent.Future

/**
  * Created by Ilya Volynin on 26.10.2018 at 14:40.
  */
object ImplicitExtensions {

  implicit class StringExt[A](s: String) {

    def respondWith[T[A] <: Request[A]](request: T[A], block: T[A] => Future[Result]): Future[Result]
    = if (s.isEmpty) block(request) else Future.successful(Results.Ok(views.html.error(s)))
  }

}
