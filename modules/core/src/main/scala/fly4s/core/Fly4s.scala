package fly4s.core

import cats.effect.Async
import fly4s.core.data._
import org.flywaydb.core.Flyway

class Fly4s(flyway: Flyway) {

  /** Return the Java `Flyway` instance
    */
  val toJava: Flyway = flyway

  /** <p>Starts the database migration. All pending migrations will be applied in order.
    * Calling migrate on an up-to-date database has no effect.</p>
    * <img src="https://flywaydb.org/assets/balsamiq/command-migrate.png" alt="migrate">
    *
    * @return An object summarising the successfully applied migrations.
    */
  def migrate[F[_]: Async]: F[MigrateResult] = Async[F].delay { flyway.migrate() }

  /** <p>Undoes the most recently applied versioned migration. If target is specified, Flyway will attempt to undo
    * versioned migrations in the order they were applied until it hits one with a version below the target. If there
    * is no versioned migration to undo, calling undo has no effect.</p>
    * <p><i>Flyway Teams only</i></p>
    * <img src="https://flywaydb.org/assets/balsamiq/command-undo.png" alt="undo">
    *
    * @return An object summarising the successfully undone migrations.
    */
  def undo[F[_]: Async]: F[UndoResult] = Async[F].delay { flyway.undo() }

  /** <p>Validate applied migrations against resolved ones (on the filesystem or classpath)
    * to detect accidental changes that may prevent the schema(s) from being recreated exactly.</p>
    * <p>Validation fails if</p>
    * <ul>
    * <li>differences in migration names, types or checksums are found</li>
    * <li>versions have been applied that aren't resolved locally anymore</li>
    * <li>versions have been resolved that haven't been applied yet</li>
    * </ul>
    *
    * <img src="https://flywaydb.org/assets/balsamiq/command-validate.png" alt="validate">
    *
    * @return An object summarising the validation results
    */
  def validate[F[_]: Async]: F[ValidateResult] = Async[F].delay { flyway.validateWithResult() }

  /** <p>Drops all objects (tables, views, procedures, triggers, ...) in the configured schemas.
    * The schemas are cleaned in the order specified by the `schemas` property.</p>
    * <img src="https://flywaydb.org/assets/balsamiq/command-clean.png" alt="clean">
    *
    * @return An object summarising the actions taken
    */
  def clean[F[_]: Async]: F[CleanResult] = Async[F].delay { flyway.clean() }

  /** <p>Retrieves the complete information about all the migrations including applied, pending and current migrations with
    * details and status.</p>
    * <img src="https://flywaydb.org/assets/balsamiq/command-info.png" alt="info">
    *
    * @return All migrations sorted by version, oldest first.
    */
  def info[F[_]: Async]: F[MigrationInfoService] = Async[F].delay { flyway.info() }

  /** <p>Baselines an existing database, excluding all migrations up to and including baselineVersion.</p>
    *
    * <img src="https://flywaydb.org/assets/balsamiq/command-baseline.png" alt="baseline">
    *
    * @return An object summarising the actions taken
    */
  def baseline[F[_]: Async]: F[BaselineResult] = Async[F].delay { flyway.baseline() }

  /** Repairs the Flyway schema history table. This will perform the following actions:
    * <ul>
    * <li>Remove any failed migrations on databases without DDL transactions (User objects left behind must still be cleaned up manually)</li>
    * <li>Realign the checksums, descriptions and types of the applied migrations with the ones of the available migrations</li>
    * </ul>
    * <img src="https://flywaydb.org/assets/balsamiq/command-repair.png" alt="repair">
    *
    * @return An object summarising the actions taken
    */
  def repair[F[_]: Async]: F[RepairResult] = Async[F].delay { flyway.repair() }
}

object Fly4s extends AllInstances with AllSyntax {

  def apply(config: Fly4sConfig): Fly4s =
    Fly4s.fromJava(new Flyway(config.toJava))

  def fromJava(flyway: Flyway): Fly4s =
    new Fly4s(flyway)
}
