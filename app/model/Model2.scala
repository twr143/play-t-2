package model
import ai.x.play.json.Jsonx
import play.api.libs.json.{Json, OFormat}
import ai.x.play.json.SingletonEncoder.simpleName
import ai.x.play.json.implicits.formatSingleton
import ai.x.play.json.implicits.optionNoError

/**
  * Created by Ilya Volynin on 19.10.2019 at 11:58.
  */
object Model2 {

  abstract class Myresp[T](t: T)

  case class MyResp1(t: Req1) extends Myresp[Req1](t: Req1)

  case class MyResp2(t: Req2) extends Myresp[Req2](t: Req2)

  //  case class MyResp3(t: Req3) extends Myresp[Req3](t: Req3)
  sealed trait AbstractRequest

  //  case class Req3(first: Int, second: Int, third: Int) extends AbstractRequest
  case class Req2(first: Option[Int], fourth: Int) extends AbstractRequest

  case class Req1(first: Option[Int], second: Option[Int], third: Option[Int]) extends AbstractRequest

  //  implicit def formatT[T] = Jsonx.oFormatSealed[T]
  //  implicit lazy val req3Format  = Json.writes[Req3]
  implicit lazy val req2Format = Json.writes[Req2]

  implicit lazy val req1Format = Json.writes[Req1]

  implicit def respFormatR1 = Json.writes[MyResp1]

  implicit def respFormatR2 = Json.writes[MyResp2]

  //    implicit def respFormatR3 = Json.writes[MyResp3]
  implicit lazy val aReq = {
    //    implicit lazy val req3Format: OFormat[Req3] = Jsonx.formatCaseClass[Req3]
    implicit lazy val req2Format: OFormat[Req2] = Jsonx.formatCaseClass[Req2]
    implicit lazy val req1Format: OFormat[Req1] = Jsonx.formatCaseClass[Req1]
    Jsonx.oFormatSealed[AbstractRequest]
  }
}
