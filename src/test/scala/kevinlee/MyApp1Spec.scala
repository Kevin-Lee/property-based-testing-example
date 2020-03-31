package kevinlee

import argonaut._

import hedgehog._
import hedgehog.runner._

import kevinlee.Person.PhoneNumber

/**
 * @author Kevin Lee
 */
object MyApp1Spec extends Properties {

  override def tests: List[Test] = List(
    property("test add", testAdd),
    property("test reverse", testReverse),
    property("test substring", testSubstring),
    property("round-trip test for Person JSON encoder and decoder", testPersonJson)
  )

  def testAdd: Property = for {
    a <- Gen.int(Range.linear(1, 20)).log("a")
    b <- Gen.int(Range.linear(1, 20)).log("b")
  } yield {
    MyApp1.add(a, b) ==== a + b
  }

  def testReverse: Property = for {
    s <- Gen.string(Gen.alphaNum, Range.linear(1, 50)).log("s")
  } yield {
    Result.diffNamed("reverse == reverse", MyApp1.reverse(s), MyApp1.reverse(s))(_ == _) and
      Result.diffNamed("reverse(s) twice == s", MyApp1.reverse(MyApp1.reverse(s)), s)(_ == _)

  }

  def testSubstring: Property = for {
    x <- Gen.string(Gen.alpha, Range.linear(0, 10)).log("x")
    y <- Gen.string(Gen.alpha, Range.linear(0, 10)).log("y")
  } yield {
    MyApp1.substring(x + y, x.length) ==== y
  }

  def genPerson: Gen[Person] = for {
    id <- Gen.long(Range.linear(1L, 100L)).map(Person.Id)
    firstName <- Gen.string(Gen.alpha, Range.linear(1, 10)).map(Person.FirstName)
    lastName <- Gen.string(Gen.alpha, Range.linear(1, 10)).map(Person.LastName)
    phoneNumber <- Gen.element1("1111-1111", "1234-5678").map(PhoneNumber).option
    n <- Gen.int(Range.linear(1, 999))
    address <- Gen.string(Gen.alphaNum, Range.linear(1, 20)).map(x => s"$n$x").map(Person.Address).option
  } yield Person(id, firstName, lastName, phoneNumber, address)

  def testPersonJson: Property = for {
    person <- genPerson.log("person")
  } yield {
    val json = MyApp1.encoder.encode(person).spaces2
//    println(s"person: $person")

    (Parse.decodeEither(json)(MyApp1.decorder)) match {
      case Left(error) =>
        Result.failure.log(s"error: $error")
      case Right(actual) =>
        actual ==== person
      }
  }

}
