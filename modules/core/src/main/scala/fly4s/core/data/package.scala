package fly4s.core

import cats.data.ValidatedNel
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.flywaydb.core.api.output.{MigrateResult => JMigrateResult, ValidateOutput => JValidateOutput}

package object data {
  type FlywayConfiguration = FluentConfiguration
  type MigrateResult = JMigrateResult
  type ValidateOutput = JValidateOutput
  type ValidatedMigrateResult = ValidatedNel[ValidateOutput, MigrateResult]
}
