package fly4s.core

import cats.data.ValidatedNel
import org.flywaydb.core.api.{
  ErrorCode => JErrorCode,
  ErrorDetails => JErrorDetails,
  Location => JLocation,
  MigrationInfoService => JMigrationInfoService,
  MigrationVersion => JMigrationVersion,
  ResourceProvider => JResourceProvider
}
import org.flywaydb.core.api.output.{
  BaselineResult => JBaselineResult,
  CleanResult => JCleanResult,
  MigrateResult => JMigrateResult,
  RepairResult => JRepairResult,
  UndoResult => JUndoResult,
  ValidateOutput => JValidateOutput,
  ValidateResult => JValidateResult
}

import org.flywaydb.core.api.resolver.{MigrationResolver => JMigrationResolver}

import org.flywaydb.core.api.callback.{Callback => JCallback}

package object data {

  //results
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
  //conf
  type Location          = JLocation
  type MigrationVersion  = JMigrationVersion
  type Callback          = JCallback
  type MigrationResolver = JMigrationResolver
  type ResourceProvider  = JResourceProvider
}
