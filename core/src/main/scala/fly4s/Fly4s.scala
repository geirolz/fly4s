package fly4s

import cats.Endo
import cats.data.Validated
import cats.data.Validated.Valid
import fly4s.data.*
import fly4s.data.Fly4sConfig
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.{Configuration, FluentConfiguration}

import javax.sql.DataSource

sealed trait Fly4s[F[_]] {

  // ------------------------------------- CONFIG -------------------------------------
  /** Get che Fly4s configuration used to build this instance.
    */
  def config: Fly4sConfig

  /** Retrieves the url, user and password used to construct the dataSource. May be `None` if the
    * dataSource was passed in directly.
    *
    * @return
    *   The url, user and password used to construct the dataSource. May be `None` if the dataSource
    *   was passed in directly.
    */
  def sourceConfig: SourceConfig

  /** Re-instantiate a [[Fly4s]] instance with the new configuration
    * @param newConfig
    *   New configuration instance
    * @return
    *   [[Fly4s]] instance with the new configuration
    */
  def reconfigure(newConfig: Fly4sConfig): F[Fly4s[F]]

  /** Re-instantiate a [[Fly4s]] instance with the updated configuration
    * @param updateConfig
    *   Function to update configuration
    * @return
    *   [[Fly4s]] instance with the updated configuration
    */
  def reconfigure(updateConfig: Endo[Fly4sConfig]): F[Fly4s[F]] =
    reconfigure(updateConfig(config))

  // ------------------------------------- OPS -------------------------------------
  /** Validate and then runs migrations.
    *
    * <b><i>1. Validation</i></b> To apply the validation you should reconfigure the [[Fly4s]] with
    * `ignorePendingMigrations` set as `true` Check [[Fly4s.validate]] for further details
    *
    * <b><i>2. Migration</i></b> If validation steps fails migration wont be applied. Check
    * [[Fly4s.migrate]] for further details
    *
    * @return
    *   An `ValidatedNel` summarising the operation results.
    */
  def validateAndMigrate: F[ValidatedMigrateResult]

  /** <p>Starts the database migration. All pending migrations will be applied in order. Calling
    * migrate on an up-to-date database has no effect.</p> <img
    * src="https://flywaydb.org/assets/balsamiq/command-migrate.png" alt="migrate">
    *
    * @return
    *   An object summarising the successfully applied migrations.
    */
  def migrate: F[MigrateResult]

  /** <p>Undoes the most recently applied versioned migration. If target is specified, Flyway will
    * attempt to undo versioned migrations in the order they were applied until it hits one with a
    * version below the target. If there is no versioned migration to undo, calling undo has no
    * effect.</p> <p><i>Flyway Teams only</i></p> <img
    * src="https://flywaydb.org/assets/balsamiq/command-undo.png" alt="undo">
    *
    * @return
    *   An object summarising the successfully undone migrations.
    */
  def undo: F[OperationResult]

  /** <p>Validate applied migrations against resolved ones (on the filesystem or classpath) to
    * detect accidental changes that may prevent the schema(s) from being recreated exactly.</p>
    * <p>Validation fails if</p> <ul> <li>differences in migration names, types or checksums are
    * found</li> <li>versions have been applied that aren't resolved locally anymore</li>
    * <li>versions have been resolved that haven't been applied yet</li> </ul>
    *
    * <img src="https://flywaydb.org/assets/balsamiq/command-validate.png" alt="validate">
    *
    * @return
    *   An object summarising the validation results
    */
  def validate: F[ValidateResult]

  /** <p>Drops all objects (tables, views, procedures, triggers, ...) in the configured schemas. The
    * schemas are cleaned in the order specified by the `schemas` property.</p> <img
    * src="https://flywaydb.org/assets/balsamiq/command-clean.png" alt="clean">
    *
    * @return
    *   An object summarising the actions taken
    */
  def clean: F[CleanResult]

  /** <p>Retrieves the complete information about all the migrations including applied, pending and
    * current migrations with details and status.</p> <img
    * src="https://flywaydb.org/assets/balsamiq/command-info.png" alt="info">
    *
    * @return
    *   All migrations sorted by version, oldest first.
    */
  def info: F[MigrationInfoService]

  /** <p>Baselines an existing database, excluding all migrations up to and including
    * baselineVersion.</p>
    *
    * <img src="https://flywaydb.org/assets/balsamiq/command-baseline.png" alt="baseline">
    *
    * @return
    *   An object summarising the actions taken
    */
  def baseline: F[BaselineResult]

  /** Repairs the Flyway schema history table. This will perform the following actions: <ul>
    * <li>Remove any failed migrations on databases without DDL transactions (User objects left
    * behind must still be cleaned up manually)</li> <li>Realign the checksums, descriptions and
    * types of the applied migrations with the ones of the available migrations</li> </ul> <img
    * src="https://flywaydb.org/assets/balsamiq/command-repair.png" alt="repair">
    *
    * @return
    *   An object summarising the actions taken
    */
  def repair: F[RepairResult]

  /** Close and release datasource connection. This method is private to avoid problems, indeed once
    * called this method this `Fly4s` instance is not usable anymore
    */
  private[fly4s] def close: F[Unit]
}
object Fly4s extends AllInstances {

  import cats.effect.*
  import cats.implicits.*

  /** Creates a `Resource` to properly handle the connection with the datasource Create a new
    * `javax.sql` using fly4s `DriverDataSource` with the specified parameters.
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
  ): Resource[F, Fly4s[F]] =
    Unsafe.makeFromRawConfigForDataSource[F](
      mapFlywayConfig = _.dataSource(url, user.orNull, password.map(_.mkString).orNull),
      config          = config,
      classLoader     = classLoader
    )

  /** Creates a new [[Fly4s]] `Resource` to properly handle the connection with the datasource. Sets
    * the datasource to use. Must have the necessary privileges to execute DDL.
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
  ): Resource[F, Fly4s[F]] =
    Resource
      .eval(acquireDataSource)
      .flatMap(ds => {
        Unsafe.makeFromRawConfigForDataSource[F](
          mapFlywayConfig = _.dataSource(ds),
          config          = config,
          classLoader     = classLoader
        )
      })

  private[fly4s] object Unsafe {

    def makeFromRawConfigForDataSource[F[_]](
      mapFlywayConfig: Endo[FluentConfiguration],
      config: Fly4sConfig      = Fly4sConfig.default,
      classLoader: ClassLoader = Thread.currentThread.getContextClassLoader
    )(implicit F: Async[F]): Resource[F, Fly4s[F]] = {

      val acquireFly4s = for {
        c1    <- Fly4sConfig.toJavaF[F](config, classLoader)
        c2    <- F.delay(Flyway.configure(classLoader).configuration(c1))
        fly4s <- fromJavaConfig[F](mapFlywayConfig(c2))
      } yield fly4s

      Resource.make[F, Fly4s[F]](acquireFly4s)(_.close)
    }

    def fromJavaConfig[F[_]](configuration: Configuration)(implicit F: Async[F]): F[Fly4s[F]] =
      F.delay {
        val flyway = new Flyway(configuration)
        new Fly4sImpl[F](
          flyway,
          Fly4sConfig.fromJava(flyway.getConfiguration)
        )
      }

    final class Fly4sImpl[F[_]](
      private val flyway: Flyway,
      override val config: Fly4sConfig
    )(implicit F: Async[F])
        extends Fly4s[F] {

      import cats.implicits.*

      // ------------------------------------- CONFIG -------------------------------------
      def sourceConfig: SourceConfig = {
        val jconf = flyway.getConfiguration
        SourceConfig.fromNullable(
          url      = jconf.getUrl,
          user     = jconf.getUser,
          password = jconf.getPassword
        )
      }

      override def reconfigure(newConfig: Fly4sConfig): F[Fly4s[F]] =
        for {
          currentJConfig <- F.pure(flyway.getConfiguration)
          classLoader = currentJConfig.getClassLoader
          c <- Fly4sConfig.toJavaF[F](newConfig, classLoader)
          jConfig <- F.delay {

            val newJConfig = new FluentConfiguration(classLoader)
              .configuration(c)

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

      // ------------------------------------- OPS -------------------------------------
      override def validateAndMigrate: F[ValidatedMigrateResult] =
        for {
          validateResult   <- validate
          validationResNel <- ValidateResult.toValidatedNel[F](validateResult)
          migrationRes <- validationResNel match {
            case Valid(_)                 => migrate.map(_.valid)
            case i @ Validated.Invalid(_) => F.pure(i)
          }
        } yield migrationRes

      override def migrate: F[MigrateResult] =
        F.blocking { flyway.migrate() }

      override def undo: F[OperationResult] =
        F.blocking { flyway.undo() }

      override def validate: F[ValidateResult] =
        F.blocking { flyway.validateWithResult() }

      override def clean: F[CleanResult] =
        F.blocking { flyway.clean() }

      override def info: F[MigrationInfoService] =
        F.blocking { flyway.info() }

      override def baseline: F[BaselineResult] =
        F.blocking { flyway.baseline() }

      override def repair: F[RepairResult] =
        F.blocking { flyway.repair() }

      override private[fly4s] def close: F[Unit] =
        F.delay { flyway.getConfiguration.getDataSource.getConnection.close() }
    }

  }
}
