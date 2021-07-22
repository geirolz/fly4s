package fly4s.core

import cats.data.{NonEmptyList, ValidatedNel}
import cats.data.Validated.{Invalid, Valid}
import cats.effect.Async
import fly4s.core.data.{FlywayConfiguration, ValidateOutput, ValidatedMigrateResult}
import fly4s.core.syntax.AllSyntax
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location

import scala.jdk.CollectionConverters.CollectionHasAsScala

//noinspection SimplifyBooleanMatch
object Fly4s extends AllBasicInstances with AllSyntax {

  import cats.implicits._

  def migrate[F[_]](
    url: String,
    user: Option[String] = None,
    pass: Option[Array[Char]] = None,
    migrationsTable: String = "FlywaySchemaHistory",
    migrationsLocations: List[String],
    group: Boolean = false,
    outOfOrder: Boolean = false,
    baselineOnMigrate: Boolean = false,
    ignorePendingMigrations: Boolean = false
  )(implicit F: Async[F]): F[ValidatedMigrateResult] =
    F.delay {
      Flyway.configure
        .dataSource(
          url,
          user.orNull,
          pass.map(_.mkString).orNull
        )
        .group(group)
        .outOfOrder(outOfOrder)
        .baselineOnMigrate(baselineOnMigrate)
        .ignorePendingMigrations(ignorePendingMigrations)
        .table(migrationsTable)
        .locations(
          migrationsLocations.map(new Location(_)): _*
        )
    }.flatMap(migrate[F](_))

  def migrate[F[_]](
    config: FlywayConfiguration
  )(implicit F: Async[F]): F[ValidatedMigrateResult] = {

    def initFlyway(flywayConfig: FlywayConfiguration): F[ValidatedNel[ValidateOutput, Flyway]] =
      for {
        flyway         <- F.delay(flywayConfig.load())
        validateResult <- F.delay(flyway.validateWithResult())
        res <- validateResult.validationSuccessful match {
          case true => F.pure(flyway.valid)
          case false =>
            NonEmptyList
              .fromList(validateResult.invalidMigrations.asScala.toList)
              .map(_.invalid)
              .liftTo[F](new RuntimeException(""))
        }
      } yield res

    for {
      validatedFlyway <- initFlyway(config)
      result <- validatedFlyway match {
        case Valid(flyway)        => F.delay(flyway.migrate().valid)
        case invalid @ Invalid(_) => F.pure(invalid)
      }
    } yield result
  }
}
