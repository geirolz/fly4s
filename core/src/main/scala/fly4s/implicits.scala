package fly4s

import cats.{MonadThrow, Show}
import fly4s.data.{MigrateResult, ValidateOutput, ValidateOutputInstances, ValidatedMigrateResult}

object implicits extends AllInstances with AllSyntax

object instances extends AllInstances
private[fly4s] trait AllInstances extends ValidateOutputInstances

object syntax extends AllSyntax
private[fly4s] trait AllSyntax {

  import cats.implicits.*

  implicit class ValidatedMigrateResultOps(result: ValidatedMigrateResult) {
    def liftTo[F[_]: MonadThrow](implicit show: Show[Iterable[ValidateOutput]]): F[MigrateResult] =
      ValidatedMigrateResult.liftTo[F](result)
  }

  implicit class ValidatedMigrateResultMonadThrowOps[F[_]: MonadThrow](
    r: F[ValidatedMigrateResult]
  ) {
    def result(implicit show: Show[Iterable[ValidateOutput]]): F[MigrateResult] =
      r.flatMap(_.liftTo[F])
  }
}
