package tcder.detailed

import cats.syntax.all.*
import scala.Tuple.Union
import scala.compiletime as ct
import scala.deriving.Mirror

/** Noel Welsh masterclass & pair coding 19.10.2023
  */
trait Decoder[A]:
  def decode(raw: Map[String, String]): Option[A]

trait Parameter[A]:
  def decode(raw: String): Option[A]

object Parameter:
  given Parameter[Int] with
    def decode(raw: String): Option[Int] = raw.toIntOption

  given Parameter[String] with
    def decode(raw: String): Option[String] = raw.some

object Decoder:
  inline def derived[A](using m: Mirror.Of[A]): Decoder[A] =
    inline m match
      case s: Mirror.SumOf[A]     => ???
      case p: Mirror.ProductOf[A] =>
        (raw: Map[String, String]) =>
          type MPT = Tuple.Map[p.MirroredElemTypes, Parameter] // (String, Int)
          val elemParameters: MPT = ct.summonAll[MPT]
          val elemNames: p.MirroredElemLabels = ct.constValueTuple[p.MirroredElemLabels]
          val es: List[Union[elemNames.type]] = elemNames.toList
          val ps: List[Union[elemParameters.type]] = elemParameters.toList
          val ts: List[(Union[elemNames.type], Union[elemParameters.type])] = es zip ps
          val tst: List[(String, Parameter[_])] = ts.asInstanceOf[List[(String, Parameter[?])]]
          val mt = tst.map { case (name, param) =>
            val t1: String = name
            val t2: Parameter[_] = param
            val value: Option[String] = raw.get(name)            // maybe we have a key
            val decoded: Option[?] = value.flatMap(param.decode) // maybe we can decode it successfully
            decoded
          }
          val x = mt.sequence
            .map(v =>
              p.fromTuple(
                Tuple.fromArray(v.toArray).asInstanceOf[p.MirroredElemTypes]
              )
            )
          x

final case class User(name: String, score: Int) derives Decoder

@main def main() =
  val raw = Map("name" -> "Tom", "score" -> "24")
  println(summon[Decoder[User]].decode(raw))
  println(Decoder.derived[User].decode(raw))
