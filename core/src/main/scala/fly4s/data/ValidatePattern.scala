package fly4s.data

import org.flywaydb.core.api.pattern.ValidatePattern as JValidatePattern
import org.flywaydb.core.api.MigrationState

import scala.util.Try

object ValidatePattern {

  /** Require FlywayTeams.
    */
  sealed trait ValidMigrationType
  object ValidMigrationType {
    case object Repeatable extends ValidMigrationType
    case object Versioned extends ValidMigrationType
  }

  sealed trait ValidMigrationState
  object ValidMigrationState {
    case object MissingSuccess extends ValidMigrationState
    case object Pending extends ValidMigrationState
    case object Ignored extends ValidMigrationState
    case object FutureSuccess extends ValidMigrationState
  }

  val ignoreMissingSuccessMigrations: ValidatePattern =
    of(Some(ValidMigrationState.MissingSuccess))

  val ignorePendingMigrations: ValidatePattern =
    of(Some(ValidMigrationState.Pending))

  val ignoreIgnoredMigrations: ValidatePattern =
    of(Some(ValidMigrationState.Ignored))

  val ignoreFutureSuccessMigrations: ValidatePattern =
    of(Some(ValidMigrationState.FutureSuccess))

  def of(state: Option[ValidMigrationState] = None): ValidatePattern =
    of(typ = None, state = state).get

  def ofTeams(
    typ: ValidMigrationType,
    state: Option[ValidMigrationState] = None
  ): Try[ValidatePattern] =
    of(typ = Some(typ), state = state)

  private def of(
    typ: Option[ValidMigrationType],
    state: Option[ValidMigrationState]
  ): Try[ValidatePattern] = {

    val wildcard: String = "*"
    val typString = typ
      .map {
        case ValidMigrationType.Repeatable => "repeatable"
        case ValidMigrationType.Versioned  => "versioned"
      }
      .getOrElse(wildcard)

    val stateString = state
      .map {
        case ValidMigrationState.MissingSuccess => MigrationState.MISSING_SUCCESS
        case ValidMigrationState.Pending        => MigrationState.PENDING
        case ValidMigrationState.Ignored        => MigrationState.IGNORED
        case ValidMigrationState.FutureSuccess  => MigrationState.FUTURE_SUCCESS
      }
      .map(_.getDisplayName.toLowerCase())
      .getOrElse(wildcard)

    Try(JValidatePattern.fromPattern(s"$typString:$stateString"))
  }

  def fromPattern(pattern: String): Try[ValidatePattern] =
    Try(JValidatePattern.fromPattern(pattern))

  def toPattern(jvalidatePattern: JValidatePattern): Try[String] = Try {

    val c                  = classOf[JValidatePattern]
    val migrationTypeField = c.getDeclaredField("migrationType")
    migrationTypeField.setAccessible(true)
    val migrationType =
      migrationTypeField
        .get(jvalidatePattern)
        .asInstanceOf[String]

    val migrationStateField = c.getDeclaredField("migrationState")
    migrationStateField.setAccessible(true)
    val migrationState =
      migrationStateField
        .get(jvalidatePattern)
        .asInstanceOf[String]

    s"$migrationType:$migrationState"
  }
}
