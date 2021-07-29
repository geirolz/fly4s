package fly4s.core

import cats.{MonadThrow, Show}
import fly4s.core.data.{MigrateResult, ValidateOutput, ValidatedMigrateResult}

import scala.annotation.unused

trait AllCoreSyntax {

  import cats.implicits.*

  implicit class ValidatedMigrateResultOps(result: ValidatedMigrateResult) {
    def liftTo[F[_]: MonadThrow](implicit @unused S: Show[Iterable[ValidateOutput]]): F[MigrateResult] =
      ValidatedMigrateResult.liftTo[F](result)
  }

  implicit class ValidatedMigrateResultMonadThrowOps[F[_]: MonadThrow](r: F[ValidatedMigrateResult]) {
    def result(implicit @unused S: Show[Iterable[ValidateOutput]]): F[MigrateResult] = r.flatMap(_.liftTo[F])
  }
}
