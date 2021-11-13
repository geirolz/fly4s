package fly4s.core

import cats.data.ValidatedNel
import org.flywaydb.core.api.{
  ErrorCode as JErrorCode,
  ErrorDetails as JErrorDetails,
  Location as JLocation,
  MigrationInfoService as JMigrationInfoService,
  MigrationVersion as JMigrationVersion,
  ResourceProvider as JResourceProvider
}
import org.flywaydb.core.api.callback.Callback as JCallback
import org.flywaydb.core.api.output.{
  BaselineResult as JBaselineResult,
  CleanResult as JCleanResult,
  MigrateResult as JMigrateResult,
  RepairResult as JRepairResult,
  UndoResult as JUndoResult,
  ValidateOutput as JValidateOutput,
  ValidateResult as JValidateResult
}
import org.flywaydb.core.api.pattern.ValidatePattern as JValidatePattern
import org.flywaydb.core.api.resolver.MigrationResolver as JMigrationResolver

package object data {

  // results
  type ValidatedMigrateResult = ValidatedNel[ValidateOutput, MigrateResult]
  type MigrationInfoService   = JMigrationInfoService
  type MigrateResult          = JMigrateResult
  type ValidateOutput         = JValidateOutput
  type ValidateResult         = JValidateResult
  type CleanResult            = JCleanResult
  type UndoResult             = JUndoResult
  type BaselineResult         = JBaselineResult
  type RepairResult           = JRepairResult
  type ErrorDetails           = JErrorDetails
  type ErrorCode              = JErrorCode
  // conf
  type Location          = JLocation
  type MigrationVersion  = JMigrationVersion
  type Callback          = JCallback
  type MigrationResolver = JMigrationResolver
  type ResourceProvider  = JResourceProvider
  type ValidatePattern   = JValidatePattern
}
