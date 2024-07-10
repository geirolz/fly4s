package fly4s.data

import cats.{MonadThrow, Show}
import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}

object ValidatedMigrateResult {

  import cats.implicits.*

  def valid(migrateResult: MigrateResult): ValidatedMigrateResult =
    Valid(migrateResult)

  def invalid(validateOutputs: NonEmptyList[ValidateOutput]): ValidatedMigrateResult =
    Invalid(validateOutputs)

  def liftTo[F[_]](
    result: ValidatedMigrateResult
  )(implicit F: MonadThrow[F], S: Show[Iterable[ValidateOutput]]): F[MigrateResult] =
    result match {
      case Valid(result)   => F.pure(result)
      case Invalid(errors) => F.raiseError(new RuntimeException(errors.toIterable.show))
    }
}
