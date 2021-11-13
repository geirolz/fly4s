package fly4s.core.data

import cats.Functor

object Location {

  def apply(value: String): Location =
    new Location(value)

  def one(values: String): List[Location] =
    of(values)

  def of(values: String*): List[Location] =
    of(values.toList)

  def of[F[_]: Functor](values: F[String]): F[Location] =
    Functor[F].map(values)(Location(_))
}
