package fly4s.core.syntax.data

import cats.{MonadError, Show}
import fly4s.core.data.{MigrateResult, ValidateOutput, ValidatedMigrateResult}

private[data] trait ValidatedMigrateResultSyntax {

  import cats.implicits._

  implicit class ValidatedMigrateResultOps(result: ValidatedMigrateResult) {

    def liftTo[F[_]](implicit F: MonadError[F, Throwable], S: Show[Iterable[ValidateOutput]]): F[MigrateResult] =
      ValidatedMigrateResult.liftTo[F](result)
  }

  implicit class ValidatedMigrateResultFOps[F[_]](result: F[ValidatedMigrateResult])(implicit
    F: MonadError[F, Throwable]
  ) {
    def value(implicit S: Show[Iterable[ValidateOutput]]): F[MigrateResult] = result.flatMap(_.liftTo[F])
  }
}
