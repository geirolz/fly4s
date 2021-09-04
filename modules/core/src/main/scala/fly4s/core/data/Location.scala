package fly4s.core.data

import cats.Functor

object Location {

  def apply(value: String): Location =
    new Location(value)

  def one(values: String): List[Location] =
    ofAll(values)

  def ofAll(values: String*): List[Location] =
    ofFunctor(values.toList)

  def ofFunctor[F[_]: Functor](values: F[String]): F[Location] =
    Functor[F].map(values)(Location(_))
}
