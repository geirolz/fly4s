package fly4s

import cats.data.ValidatedNel
import org.flywaydb.core.{Flyway => JFlyway}
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.flywaydb.core.api.output.{MigrateResult => JMigrateResult, ValidateOutput => JValidateOutput}

package object core {
  type Flyway = JFlyway
  type FlywayConfiguration = FluentConfiguration
  type MigrateResult = JMigrateResult
  type ValidateOutput = JValidateOutput
  type MigrationResult = ValidatedNel[ValidateOutput, MigrateResult]
}
