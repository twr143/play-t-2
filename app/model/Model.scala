package model
import ai.x.play.json.Jsonx
import play.api.libs.json.{Json, OFormat}
import ai.x.play.json.SingletonEncoder.simpleName
import ai.x.play.json.implicits.formatSingleton

/**
  * Created by Ilya Volynin on 04.10.2019 at 12:39.
  */
object Model {

  sealed trait AbstractPerson

  case class Street(name: String, number: Int)

  case class Address(street: Street, city: String, postcode: String)

  case class Person(name: String, age: Int, address: Address) extends AbstractPerson

  case class PersonL(last: Int, age: Int, address: Address) extends AbstractPerson

  case class AntiPerson(last: Int, middle: Int, age: Int, address: Address) extends AbstractPerson

  val person = Person("Ilya V", 37, Address(Street("Lebedev", 7), "Yaroslavl", "150000"))

  implicit lazy val streetFormat = Jsonx.formatCaseClass[Street]

  implicit lazy val addressFormat = Jsonx.formatCaseClass[Address]

  implicit lazy val aPersonFormat = {
    implicit lazy val personFormat: OFormat[Person] = Jsonx.formatCaseClass[Person]
    implicit lazy val antiPersonFormat: OFormat[AntiPerson] = Jsonx.formatCaseClass[AntiPerson]
    implicit lazy val personLFormat: OFormat[PersonL] = Jsonx.formatCaseClass[PersonL]
    Jsonx.oFormatSealed[AbstractPerson]
  }
}
