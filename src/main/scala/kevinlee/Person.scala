package kevinlee

import Person._

/**
 * @author Kevin Lee
 */
case class Person(
  id: Id,
  firstName: FirstName,
  lastName: LastName,
  phoneNumber: Option[PhoneNumber],
  address: Option[Address]
)

object Person {
  final case class Id(id: Long) extends AnyVal
  final case class FirstName(firstName: String) extends AnyVal
  final case class LastName(lastName: String) extends AnyVal
  
  final case class PhoneNumber(phoneNumber: String) extends AnyVal
  final case class Address(address: String) extends AnyVal
  
}
