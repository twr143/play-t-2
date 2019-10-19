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
  implicit class RichResult (result: Result) {
    def enableCors =  result/*.withHeaders(
      "Access-Control-Allow-Origin" -> "*"
      , "Access-Control-Allow-Methods" -> "OPTIONS, GET, POST, PUT, DELETE, HEAD"   // OPTIONS for pre-flight
      , "Access-Control-Allow-Headers" -> "Accept, Content-Type, Origin, X-Json, X-Prototype-Version, X-Requested-With" //, "X-My-NonStd-Option"
      , "Access-Control-Allow-Credentials" -> "true"
    )                               */
  }

}
