package fly4s.data

import cats.data.NonEmptyList
import fly4s.data.Fly4sConfigDefaults.*
import org.flywaydb.core.api.configuration.Configuration

import java.nio.charset.Charset

case class Fly4sConfig(
  connectRetries: Int                       = defaultConnectRetries,
  initSql: Option[String]                   = defaultInitSql,
  defaultSchemaName: Option[String]         = defaultDefaultSchemaName,
  schemaNames: Option[NonEmptyList[String]] = defaultSchemaNames,
  lockRetryCount: Int                       = defaultLockRetryCount,
  // --- migrations ---
  installedBy: Option[String]                    = defaultInstalledBy,
  locations: List[Location]                      = defaultLocations,
  encoding: Charset                              = defaultEncoding,
  table: String                                  = defaultTable,
  tablespace: Option[String]                     = defaultTablespace,
  targetVersion: MigrationVersion                = defaultTargetVersion,
  baselineVersion: MigrationVersion              = defaultBaselineVersion,
  baselineDescription: String                    = defaultBaselineDescription,
  ignoreMigrationPatterns: List[ValidatePattern] = defaultIgnoreMigrationPatterns,
  // --- placeholders ---
  placeholders: Map[String, String] = defaultPlaceholders,
  placeholderPrefix: String         = defaultPlaceholderPrefix,
  placeholderSuffix: String         = defaultPlaceholderSuffix,
  // --- migrations naming ---
  sqlMigrationPrefix: String           = defaultSqlMigrationPrefix,
  sqlMigrationSuffixes: Seq[String]    = defaultSqlMigrationSuffixes,
  repeatableSqlMigrationPrefix: String = defaultRepeatableSqlMigrationPrefix,
  sqlMigrationSeparator: String        = defaultSqlMigrationSeparator,
  // --- migrations functions ---
  callbacks: List[Callback]                  = defaultCallbacks,
  resolvers: List[MigrationResolver]         = defaultResolvers,
  resourceProvider: Option[ResourceProvider] = defaultResourceProvider,
  // --- flags ---
  group: Boolean                   = defaultGroup,
  mixed: Boolean                   = defaultMixed,
  failOnMissingLocations: Boolean  = defaultFailOnMissingLocations,
  validateMigrationNaming: Boolean = defaultValidateMigrationNaming,
  validateOnMigrate: Boolean       = defaultValidateOnMigrate,
  cleanOnValidationError: Boolean  = defaultCleanOnValidationError,
  cleanDisabled: Boolean           = defaultCleanDisabled,
  createSchemas: Boolean           = defaultCreateSchemas,
  placeholderReplacement: Boolean  = defaultPlaceholderReplacement,
  baselineOnMigrate: Boolean       = defaultBaselineOnMigrate,
  outOfOrder: Boolean              = defaultOutOfOrder,
  skipDefaultCallbacks: Boolean    = defaultSkipDefaultCallbacks,
  skipDefaultResolvers: Boolean    = defaultSkipDefaultResolvers,
  // --- mima after 1.0.0 ---
  loggers: List[LoggerType]             = defaultLoggers,
  baseJavaConfig: Option[Configuration] = None
) extends Fly4sConfigContract
object Fly4sConfig extends Fly4sConfigBuilder:

  extension (i: Fly4sConfig)
    def withConnectRetries(connectRetries: Int): Fly4sConfig =
      i.copy(connectRetries = connectRetries)

    def withInitSql(initSql: Option[String]): Fly4sConfig =
      i.copy(initSql = initSql)

    def withDefaultSchemaName(defaultSchemaName: Option[String]): Fly4sConfig =
      i.copy(defaultSchemaName = defaultSchemaName)

    def withSchemaNames(schemaNames: Option[NonEmptyList[String]]): Fly4sConfig =
      i.copy(schemaNames = schemaNames)

    def withLockRetryCount(lockRetryCount: Int): Fly4sConfig =
      i.copy(lockRetryCount = lockRetryCount)

    def withLoggers(loggers: List[LoggerType]): Fly4sConfig =
      i.copy(loggers = loggers)

    def withInstalledBy(installedBy: Option[String]): Fly4sConfig =
      i.copy(installedBy = installedBy)

    def withLocations(locations: List[Location]): Fly4sConfig =
      i.copy(locations = locations)

    def withEncoding(encoding: Charset): Fly4sConfig =
      i.copy(encoding = encoding)

    def withTable(table: String): Fly4sConfig =
      i.copy(table = table)

    def withTablespace(tablespace: Option[String]): Fly4sConfig =
      i.copy(tablespace = tablespace)

    def withTargetVersion(targetVersion: MigrationVersion): Fly4sConfig =
      i.copy(targetVersion = targetVersion)

    def withBaselineVersion(baselineVersion: MigrationVersion): Fly4sConfig =
      i.copy(baselineVersion = baselineVersion)

    def withBaselineDescription(baselineDescription: String): Fly4sConfig =
      i.copy(baselineDescription = baselineDescription)

    def withIgnoreMigrationPatterns(
      ignoreMigrationPatterns: List[ValidatePattern]
    ): Fly4sConfig =
      i.copy(ignoreMigrationPatterns = ignoreMigrationPatterns)

    def withPlaceholders(placeholders: Map[String, String]): Fly4sConfig =
      i.copy(placeholders = placeholders)

    def withPlaceholderPrefix(placeholderPrefix: String): Fly4sConfig =
      i.copy(placeholderPrefix = placeholderPrefix)

    def withPlaceholderSuffix(placeholderSuffix: String): Fly4sConfig =
      i.copy(placeholderSuffix = placeholderSuffix)

    def withSqlMigrationPrefix(sqlMigrationPrefix: String): Fly4sConfig =
      i.copy(sqlMigrationPrefix = sqlMigrationPrefix)

    def withSqlMigrationSuffixes(sqlMigrationSuffixes: Seq[String]): Fly4sConfig =
      i.copy(sqlMigrationSuffixes = sqlMigrationSuffixes)

    def withRepeatableSqlMigrationPrefix(repeatableSqlMigrationPrefix: String): Fly4sConfig =
      i.copy(repeatableSqlMigrationPrefix = repeatableSqlMigrationPrefix)

    def withSqlMigrationSeparator(sqlMigrationSeparator: String): Fly4sConfig =
      i.copy(sqlMigrationSeparator = sqlMigrationSeparator)

    def withCallbacks(callbacks: List[Callback]): Fly4sConfig =
      i.copy(callbacks = callbacks)

    def withResolvers(resolvers: List[MigrationResolver]): Fly4sConfig =
      i.copy(resolvers = resolvers)

    def withResourceProvider(resourceProvider: Option[ResourceProvider]): Fly4sConfig =
      i.copy(resourceProvider = resourceProvider)

    def withGroup(group: Boolean): Fly4sConfig =
      i.copy(group = group)

    def withMixed(mixed: Boolean): Fly4sConfig =
      i.copy(mixed = mixed)

    def withFailOnMissingLocations(failOnMissingLocations: Boolean): Fly4sConfig =
      i.copy(failOnMissingLocations = failOnMissingLocations)

    def withValidateMigrationNaming(validateMigrationNaming: Boolean): Fly4sConfig =
      i.copy(validateMigrationNaming = validateMigrationNaming)

    def withValidateOnMigrate(validateOnMigrate: Boolean): Fly4sConfig =
      i.copy(validateOnMigrate = validateOnMigrate)

    def withCleanOnValidationError(cleanOnValidationError: Boolean): Fly4sConfig =
      i.copy(cleanOnValidationError = cleanOnValidationError)

    def withCleanDisabled(cleanDisabled: Boolean): Fly4sConfig =
      i.copy(cleanDisabled = cleanDisabled)

    def withCreateSchemas(createSchemas: Boolean): Fly4sConfig =
      i.copy(createSchemas = createSchemas)

    def withPlaceholderReplacement(placeholderReplacement: Boolean): Fly4sConfig =
      i.copy(placeholderReplacement = placeholderReplacement)

    def withBaselineOnMigrate(baselineOnMigrate: Boolean): Fly4sConfig =
      i.copy(baselineOnMigrate = baselineOnMigrate)

    def withOutOfOrder(outOfOrder: Boolean): Fly4sConfig =
      i.copy(outOfOrder = outOfOrder)

    def withSkipDefaultCallbacks(skipDefaultCallbacks: Boolean): Fly4sConfig =
      i.copy(skipDefaultCallbacks = skipDefaultCallbacks)

    def withSkipDefaultResolvers(skipDefaultResolvers: Boolean): Fly4sConfig =
      i.copy(skipDefaultResolvers = skipDefaultResolvers)
