package org.alexr

import cats.*
import cats.data.*
import cats.implicits.*
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.prop.TableFor2

class OneSpec extends AnyFunSuite with Matchers with ScalaCheckPropertyChecks {

  test("1. result asserting") {
    1 + 1 shouldBe 2
  }

  test("2. exception asserting") {
    an[NumberFormatException] shouldBe thrownBy {
      "1a".toInt
    }
  }

  test("3. exception body checking") {
    val x: NumberFormatException = the[NumberFormatException] thrownBy "1a".toInt
    x.getMessage shouldBe """For input string: "1a""""
  }

  test("4. table driven") {
    val table: TableFor2[Int, String] = Table(
      ("in", "out"),
      (1, "1"),
      (2, "2"),
      (3, "3"),
    )

    forAll(table) { (in, out) =>
      in.toString shouldBe out
    }

  }

  /** for generator driven we can specify count */
  override implicit val generatorDrivenConfig: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 11)

  test("5.1. generator driven") {
    forAll { (a: Int) =>
      a + 0 shouldEqual a
      0 + a shouldEqual a
      a - a shouldEqual 0
    }
  }

  test("5.2. generator driven") {
    forAll { (a: Int, b: Int, c: Int) =>
      ((a + b) + c) shouldEqual (a + (b + c))
    }
  }

  test("5.3. generator driven ") {
    case class Cart(count: Int, total: Double)
    implicit val combiner: Semigroup[Cart] = Semigroup.instance[Cart]((c1, c2) => Cart(c1.count + c2.count, c1.total + c2.total))

    val genInt: Gen[Int] = Gen.posNum[Int]
    implicit val arbInt: Arbitrary[Int] = Arbitrary(genInt)

    val genDbl: Gen[Double] = Gen.posNum[Double]
    implicit val arbDbl: Arbitrary[Double] = Arbitrary(genDbl)

    val genCart: Gen[Cart] = for {
      int <- genInt
      dbl <- genDbl
    } yield Cart(int, dbl)
    implicit val arbCart: Arbitrary[Cart] = Arbitrary(genCart)

    /** Cart is generated automatically based on `arbInt`, `arbDbl` thanks to `org.scalacheck.ScalacheckShapeless.derivedArbitrary`
      */
    forAll { (cart1: Cart, cart2: Cart) =>
      val cart3: Cart = cart1 |+| cart2
      pprint.pprintln((cart1, cart2, cart3), width = 1000)
      cart3.total shouldBe (cart1.total + cart2.total)
      cart3.count shouldBe (cart1.count + cart2.count)
    }

  }

}
