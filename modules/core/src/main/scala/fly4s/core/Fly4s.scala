package fly4s.core

import cats.effect.Async
import cats.Endo
import cats.data.Validated
import cats.data.Validated.Valid
import fly4s.core.data.*
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.{Configuration, FluentConfiguration}

import javax.sql.DataSource

final class Fly4s private (private val flyway: Flyway, val config: Fly4sConfig) {

  import cats.implicits.*

  /** Re-instantiate a [[Fly4s]] instance with the new configuration
    * @param newConfig
    *   New configuration instance
    * @return
    *   [[Fly4s]] instance with the new configuration
    */
  def reconfigure[F[_]: Async](newConfig: Fly4sConfig): F[Fly4s] =
    Fly4s.Unsafe.reconfigure[F](this, newConfig)

  /** Re-instantiate a [[Fly4s]] instance with the updated configuration
    * @param updateConfig
    *   Function to update configuration
    * @return
    *   [[Fly4s]] instance with the updated configuration
    */
  def reconfigure[F[_]: Async](updateConfig: Endo[Fly4sConfig]): F[Fly4s] =
    Fly4s.Unsafe.reconfigure[F](this, updateConfig(config))

  /** Validate and then runs migrations.
    *
    * <b><i>1. Validation</i></b> To apply the validation we reconfigure the [[Fly4s]] with `ignorePendingMigrations`
    * set as `true` Check [[Fly4s.validate]] for further details
    *
    * <b><i>2. Migration</i></b> If validation steps fails migration wont be applied. Check [[Fly4s.migrate]] for
    * further details
    *
    * @return
    *   An `ValidatedNel` summarising the operation results.
    */
  def validateAndMigrate[F[_]: Async]: F[ValidatedMigrateResult] = {
    for {
      validateResult   <- validate[F]
      validationResNel <- ValidateResult.toValidatedNel[F](validateResult)
      migrationRes <- validationResNel match {
        case Valid(_)                 => migrate[F].map(_.valid)
        case i @ Validated.Invalid(_) => Async[F].pure(i)
      }
    } yield migrationRes
  }

  /** <p>Starts the database migration. All pending migrations will be applied in order. Calling migrate on an
    * up-to-date database has no effect.</p> <img src="https://flywaydb.org/assets/balsamiq/command-migrate.png"
    * alt="migrate">
    *
    * @return
    *   An object summarising the successfully applied migrations.
    */
  def migrate[F[_]](implicit F: Async[F]): F[MigrateResult] = F.blocking { flyway.migrate() }

  /** <p>Undoes the most recently applied versioned migration. If target is specified, Flyway will attempt to undo
    * versioned migrations in the order they were applied until it hits one with a version below the target. If there is
    * no versioned migration to undo, calling undo has no effect.</p> <p><i>Flyway Teams only</i></p> <img
    * src="https://flywaydb.org/assets/balsamiq/command-undo.png" alt="undo">
    *
    * @return
    *   An object summarising the successfully undone migrations.
    */
  def undo[F[_]](implicit F: Async[F]): F[UndoResult] = F.blocking { flyway.undo() }

  /** <p>Validate applied migrations against resolved ones (on the filesystem or classpath) to detect accidental changes
    * that may prevent the schema(s) from being recreated exactly.</p> <p>Validation fails if</p> <ul> <li>differences
    * in migration names, types or checksums are found</li> <li>versions have been applied that aren't resolved locally
    * anymore</li> <li>versions have been resolved that haven't been applied yet</li> </ul>
    *
    * <img src="https://flywaydb.org/assets/balsamiq/command-validate.png" alt="validate">
    *
    * @return
    *   An object summarising the validation results
    */
  def validate[F[_]](implicit F: Async[F]): F[ValidateResult] = F.blocking { flyway.validateWithResult() }

  /** <p>Drops all objects (tables, views, procedures, triggers, ...) in the configured schemas. The schemas are cleaned
    * in the order specified by the `schemas` property.</p> <img
    * src="https://flywaydb.org/assets/balsamiq/command-clean.png" alt="clean">
    *
    * @return
    *   An object summarising the actions taken
    */
  def clean[F[_]](implicit F: Async[F]): F[CleanResult] = F.blocking { flyway.clean() }

  /** <p>Retrieves the complete information about all the migrations including applied, pending and current migrations
    * with details and status.</p> <img src="https://flywaydb.org/assets/balsamiq/command-info.png" alt="info">
    *
    * @return
    *   All migrations sorted by version, oldest first.
    */
  def info[F[_]](implicit F: Async[F]): F[MigrationInfoService] = F.blocking { flyway.info() }

  /** <p>Baselines an existing database, excluding all migrations up to and including baselineVersion.</p>
    *
    * <img src="https://flywaydb.org/assets/balsamiq/command-baseline.png" alt="baseline">
    *
    * @return
    *   An object summarising the actions taken
    */
  def baseline[F[_]](implicit F: Async[F]): F[BaselineResult] = F.blocking { flyway.baseline() }

  /** Repairs the Flyway schema history table. This will perform the following actions: <ul> <li>Remove any failed
    * migrations on databases without DDL transactions (User objects left behind must still be cleaned up manually)</li>
    * <li>Realign the checksums, descriptions and types of the applied migrations with the ones of the available
    * migrations</li> </ul> <img src="https://flywaydb.org/assets/balsamiq/command-repair.png" alt="repair">
    *
    * @return
    *   An object summarising the actions taken
    */
  def repair[F[_]](implicit F: Async[F]): F[RepairResult] = F.blocking { flyway.repair() }
}

object Fly4s extends AllCoreInstances {

  import cats.effect.*
  import cats.implicits.*

  /** Creates a `Resource` to properly handle the connection with the datasource Create a new `javax.sql` using fly4s
    * `DriverDataSource` with the specified parameters.
    *
    * @param url
    *   The JDBC URL of the database.
    * @param user
    *   The user of the database.
    * @param password
    *   The password of the database.
    * @param classLoader
    *   The ClassLoader to use for loading migrations, resolvers, etc from the classpath. (default:
    *   Thread.currentThread().getContextClassLoader())
    * @tparam F
    *   Async effect type
    * @return
    *   A resource that, once used, safely close the datasource
    */
  def make[F[_]: Async](
    url: String,
    user: Option[String]          = None,
    password: Option[Array[Char]] = None,
    config: Fly4sConfig           = Fly4sConfig.default,
    classLoader: ClassLoader      = Thread.currentThread.getContextClassLoader
  ): Resource[F, Fly4s] =
    Unsafe.makeFromRawConfigForDataSource[F](
      mapFlywayConfig = _.dataSource(url, user.orNull, password.map(_.mkString).orNull),
      config          = config,
      classLoader     = classLoader
    )

  /** Creates a new [[Fly4s]] `Resource` to properly handle the connection with the datasource. Sets the datasource to
    * use. Must have the necessary privileges to execute DDL.
    *
    * @param acquireDataSource
    *   The datasource to use. Must have the necessary privileges to execute DDL.
    * @tparam F
    *   Async effect type
    * @return
    *   A resource that, once used, safely close the datasource
    */
  def makeFor[F[_]: Async](
    acquireDataSource: F[DataSource],
    config: Fly4sConfig      = Fly4sConfig.default,
    classLoader: ClassLoader = Thread.currentThread.getContextClassLoader
  ): Resource[F, Fly4s] =
    Resource
      .eval(acquireDataSource)
      .flatMap(ds => {
        Unsafe.makeFromRawConfigForDataSource[F](
          mapFlywayConfig = _.dataSource(ds),
          config          = config,
          classLoader     = classLoader
        )
      })

  object Unsafe {

    def makeFromRawConfigForDataSource[F[_]](
      mapFlywayConfig: Endo[FluentConfiguration],
      config: Fly4sConfig      = Fly4sConfig.default,
      classLoader: ClassLoader = Thread.currentThread.getContextClassLoader
    )(implicit F: Async[F]): Resource[F, Fly4s] =
      Resource
        .make(
          fromJavaConfig[F](
            mapFlywayConfig(
              Flyway
                .configure(classLoader)
                .configuration(Fly4sConfig.toJava(config))
            )
          )
        )(f4s =>
          F.delay(
            f4s.flyway.getConfiguration.getDataSource.getConnection.close()
          )
        )

    def reconfigure[F[_]: Async](fly4s: Fly4s, config: Fly4sConfig): F[Fly4s] = {
      for {
        jConfig <- Async[F].delay {
          val currentJConfig = fly4s.flyway.getConfiguration
          val newJConfig =
            new FluentConfiguration(currentJConfig.getClassLoader)
              .configuration(Fly4sConfig.toJava(config))

          if (currentJConfig.getUrl == null) {
            newJConfig
              .dataSource(currentJConfig.getDataSource)
          } else {
            newJConfig
              .dataSource(
                currentJConfig.getUrl,
                currentJConfig.getUser,
                currentJConfig.getPassword
              )
          }
        }
        fly4s <- fromJavaConfig(jConfig)
      } yield fly4s
    }

    def fromJavaConfig[F[_]](configuration: Configuration)(implicit F: Async[F]): F[Fly4s] =
      F.delay {
        val flyway = new Flyway(configuration)
        new Fly4s(
          flyway,
          Fly4sConfig.fromJava(flyway.getConfiguration)
        )
      }
  }
}
