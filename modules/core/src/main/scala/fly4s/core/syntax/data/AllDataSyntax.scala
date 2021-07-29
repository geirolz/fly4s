package fly4s.core.syntax.data

import cats.{MonadThrow, Show}
import fly4s.core.data.{MigrateResult, ValidateOutput, ValidatedMigrateResult}

trait AllDataSyntax extends ValidatedMigrateResultSyntax

private[data] sealed trait ValidatedMigrateResultSyntax {

  import cats.implicits.*

  implicit class ValidatedMigrateResultOps(result: ValidatedMigrateResult) {

    def liftTo[F[_]](implicit F: MonadThrow[F], S: Show[Iterable[ValidateOutput]]): F[MigrateResult] =
      ValidatedMigrateResult.liftTo[F](result)
  }

  implicit class ValidatedMigrateResultFOps[F[_]](result: F[ValidatedMigrateResult])(implicit
    F: MonadThrow[F]
  ) {
    def value(implicit S: Show[Iterable[ValidateOutput]]): F[MigrateResult] = result.flatMap(_.liftTo[F])
  }
}
