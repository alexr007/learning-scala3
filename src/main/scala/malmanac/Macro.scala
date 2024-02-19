package malmanac

import scala.quoted.{Expr, Quotes}

object Macro {

  /** 1. implementing unit
    * everything done in compile time !!!
    * using = implicit (provided by the compiler)
    */
  //          using = implicit            quotes API constructs Expr[Unit] from literal ()
  def unitImpl(using Quotes): Expr[Unit] =
    '{ () }

  def lengthImpl(s: Expr[String])(using Quotes): Expr[Int] =
    // here is the normal Scala code

    // resulting AST:
    '{ $s.length() }
    // Apply(
    //   fun = Select(s, "length")
    //   args = List()
    // )
    //


}
