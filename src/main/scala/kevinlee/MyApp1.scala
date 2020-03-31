package kevinlee

/**
 * @author Kevin Lee
 * @since 2020-04-01
 */
object MyApp1 {
  def add(a: Int, b: Int) = if (a == 10) -1 else a + b
//  def add(a: Int, b: Int) = a + b

  def reverse(s: String): String = {
    def reverse(s: List[Char], acc: List[Char]): List[Char] = s match {
      case x :: xs => reverse(xs, x :: acc)
      case Nil => acc
    }
    reverse(s.toList, List.empty).mkString
  }

  def stringIdentity(s: String): String =
    if (s.contains('h')) "" else s

  def substring(s: String, beginIndex: Int): String =
    if (beginIndex < 0 || beginIndex > s.length)
      throw new StringIndexOutOfBoundsException(beginIndex)
    else
      s.drop(beginIndex)

  import argonaut._
  import Argonaut._

  def encoder: EncodeJson[Person] = EncodeJson { person =>
    ("id" := person.id.id) ->:
      ("firstName" := person.firstName.firstName) ->:
      ("lastName" := person.lastName.lastName) ->:
      ("phoneNumber" :?= person.phoneNumber.map(_.phoneNumber)) ->?:
      ("address" :?= person.address.map(_.address)) ->?:
      jEmptyObject
  }

  def decorder: DecodeJson[Person] = DecodeJson { c => for {
      id <- (c --\ "id").as[Long].map(Person.Id)
      firstName <- (c --\ "firstName").as[String].map(Person.FirstName)
      lastName <- (c --\ "lastName").as[String].map(Person.LastName)
      phoneNumber <- (c --\ "phoneNumber").as[Option[String]].map(_.map(Person.PhoneNumber))
      address <- (c --\ "address").as[Option[String]].map(_.map(Person.Address))
    } yield Person(id, firstName, lastName, phoneNumber, address)

  }
}
