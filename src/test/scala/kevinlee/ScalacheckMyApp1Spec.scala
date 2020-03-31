package kevinlee

import org.scalacheck._
import org.scalacheck.Prop._

/**
 * @author Kevin Lee
 */
object ScalacheckMyApp1Spec extends Properties("ScalacheckAdditionSpec") {

  val intGen: Gen[Int] = Gen.choose(1,20)

  property("add") = Prop.forAll(intGen, intGen) { (a: Int, b: Int) =>
    println(s"a: $a / b: $b")
    MyApp1.add(a, b) == a + b
  }

}
