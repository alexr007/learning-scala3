package malmanac

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import scala.quoted.*

/** [[https://https://macros.kitlangton.com]] */
class MacroPlayground extends AnyFunSuite with Matchers with ScalaCheckPropertyChecks {

  test("0") {
    trait Student
    trait Teacher
    case class PersonTyped[A](value: String)

    val jim = PersonTyped[Student]("Jim")
    val bill = PersonTyped[Teacher]("William")

    def teach(whom: PersonTyped[Student]) = ()
    def learn(from: PersonTyped[Teacher]) = ()

    // okay
    teach(jim)
    // will not compile
//    teach(bill)

    // okay
    learn(bill)
    // will not compile
//    learn(jim)
  }

  test("1-typed class usecase") {

    case class State()
    class OriginallyDesigned {
      def step1(x: Int): State = State()
      def step2(x: State): State = State()
      def step3(x: State): State = State()
      def step4(x: State): Unit = ()
    }

    case class State1()
    case class State2()
    case class State3()
    class BetterDesigned {
      def step1(x: Int): State1 = State1()
      def step2(x: State1): State2 = State2()
      def step3(x: State2): State3 = State3()
      def step4(x: State3): Unit = ()
    }

    sealed trait St
    trait Created extends St
    trait Paid extends St
    trait Delivered extends St

    case class StateTyped[A](state: State)

    class BetterWithSemantic {
      private val underline = new OriginallyDesigned
      def step1(x: Int): StateTyped[Created] = StateTyped(underline.step1(x))
      def step2(x: StateTyped[Created]): StateTyped[Paid] = StateTyped(underline.step2(x.state))
      def step3(x: StateTyped[Paid]): StateTyped[Delivered] = StateTyped(underline.step3(x.state))
      def step4(x: StateTyped[Delivered]): Unit = underline.step4(x.state)
    }

    val impl = new BetterWithSemantic
    val s1 = StateTyped[Created](State())
    // good
    impl.step2(s1)
    // will not compile
//    impl.step3(s1)
//    impl.step4(s1)

  }

  test("macro 1 - unit") {
    // macro definition    splice syntax: Expr[A] => A
    // vvv                 vvvv              vvv
    inline def unit: Unit = ${ Macro.unitImpl }

    //         using macro implementation
    pprint.log(unit)
  }

  test("macro 2 - passing a parameter") {
    //                                                String => Expr[String]
    inline def length(s: String): Int = ${ Macro.lengthImpl('s) }

    val x = length("hello")

    /** {{{
      * length("Hello")
      *
      * Apply(
      *   fun = Ident("length"),
      *   args = List(
      *            Literal(Constant("hello")),
      *            ...
      *          )
      * )
      *
      * { myString.length() }
      *
      * Apply(
      *   fun = Select(Ident("myString"), "length"),
      *   args = List()
      * )
      *
      * Apply(
      *   fun = Select(Literal(Constant("hello")), "length"),
      *   args = List()
      * )
      *
      * Apply(
      *   fun = Ident("length"),
      *   args = List(Literal(Constant("hello")))
      * )
      * }}}
      */
    pprint.log(x)
  }

  test("macro 3 - only three basic things") {
    //
    //  1. function application
    //
    val x = "Hello, World".substring(0, 5)
    //  Apply {
    //    fun =Select(
    //      Literal(Constant("Hello, World")),
    //      "substring"
    //    )
    //    args = List(
    //      Literal(Constant(0)),
    //      Literal(Constant(5))
    //    )
    //  }
    val y = 12
    //  Block {
    //    stats = ValDef {
    //              name = x,
    //              tpt = TypeRef(ThisType(TypeRef(NoPrefix(),scala)),Int),
    //              rhs = Literal(Constant( 12 ))
    //    expr = Literal(Constant( () ))
    //  }
    def f(x: Int) = x + 1
    f(12)
    // https://macros.kitlangton.com/performing-the-macro-rites
    // https://macros.kitlangton.com/macro-tactics

  }

}
