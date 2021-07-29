package fly4s.core.data

import cats.data.{NonEmptyList, ValidatedNel}
import cats.ApplicativeThrow

object ValidateResult {

  import cats.implicits.*

  import scala.jdk.CollectionConverters.CollectionHasAsScala

  def toValidatedNel[F[_]](
    v: ValidateResult
  )(implicit F: ApplicativeThrow[F]): F[ValidatedNel[ValidateOutput, Unit]] = {
    v.validationSuccessful match {
      case true => F.pure(().valid)
      case false =>
        NonEmptyList
          .fromList(v.invalidMigrations.asScala.toList)
          .map(_.invalid[Unit])
          .liftTo[F](new RuntimeException("InvalidMigrations list are empty but must be NON-empty!"))
    }
  }
}
