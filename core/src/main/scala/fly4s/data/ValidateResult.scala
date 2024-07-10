package fly4s.data

import cats.data.{NonEmptyList, ValidatedNel}
import cats.ApplicativeThrow

object ValidateResult {

  import cats.implicits.*

  import scala.jdk.CollectionConverters.CollectionHasAsScala

  def toValidatedNel[F[_]](
    v: ValidateResult
  )(implicit F: ApplicativeThrow[F]): F[ValidatedNel[ValidateOutput, Unit]] =
    if (v.validationSuccessful)
      F.pure(().valid)
    else
      NonEmptyList
        .fromList(v.invalidMigrations.asScala.toList) match {
        case Some(invalidMigrations) => invalidMigrations.invalid[Unit].pure[F]
        case None =>
          F.raiseError(
            new RuntimeException("InvalidMigrations list are empty but must be NON-empty!")
          )
      }
}
