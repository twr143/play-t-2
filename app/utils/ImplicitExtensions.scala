package utils
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Created by Ilya Volynin on 26.10.2018 at 14:40.
  */
object ImplicitExtensions {


    implicit class StringExt(s: String) {

      def toOption(): Option[String] = if (s.isEmpty) None else Some(s)
    }

}
