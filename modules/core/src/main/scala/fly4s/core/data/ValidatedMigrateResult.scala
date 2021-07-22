package fly4s.core.data

import cats.{MonadError, Show}
import cats.data.Validated.{Invalid, Valid}

object ValidatedMigrateResult {

  import cats.implicits._

  def liftTo[F[_]](
    result: ValidatedMigrateResult
  )(implicit F: MonadError[F, Throwable], S: Show[Iterable[ValidateOutput]]): F[MigrateResult] =
    result match {
      case Valid(result)   => F.pure(result)
      case Invalid(errors) => F.raiseError(new RuntimeException(errors.toIterable.show))
    }
}
