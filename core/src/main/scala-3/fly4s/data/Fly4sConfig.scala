package fly4s.data

import cats.data.NonEmptyList
import fly4s.data.Fly4sConfigDefaults.*

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
  skipDefaultResolvers: Boolean    = defaultSkipDefaultResolvers
) extends Fly4sConfigContract
object Fly4sConfig extends Fly4sConfigBuilder:

  extension(i: data.Fly4sConfig)
    def withConnectRetries(connectRetries: Int): data.Fly4sConfig =
      i.copy(connectRetries = connectRetries)

    def withInitSql(initSql: Option[String]): data.Fly4sConfig =
      i.copy(initSql = initSql)

    def withDefaultSchemaName(defaultSchemaName: Option[String]): data.Fly4sConfig =
      i.copy(defaultSchemaName = defaultSchemaName)

    def withSchemaNames(schemaNames: Option[NonEmptyList[String]]): data.Fly4sConfig =
      i.copy(schemaNames = schemaNames)

    def withLockRetryCount(lockRetryCount: Int): data.Fly4sConfig =
      i.copy(lockRetryCount = lockRetryCount)

    def withInstalledBy(installedBy: Option[String]): data.Fly4sConfig =
      i.copy(installedBy = installedBy)

    def withLocations(locations: List[Location]): data.Fly4sConfig =
      i.copy(locations = locations)

    def withEncoding(encoding: Charset): data.Fly4sConfig =
      i.copy(encoding = encoding)

    def withTable(table: String): data.Fly4sConfig =
      i.copy(table = table)

    def withTablespace(tablespace: Option[String]): data.Fly4sConfig =
      i.copy(tablespace = tablespace)

    def withTargetVersion(targetVersion: MigrationVersion): data.Fly4sConfig =
      i.copy(targetVersion = targetVersion)

    def withBaselineVersion(baselineVersion: MigrationVersion): data.Fly4sConfig =
      i.copy(baselineVersion = baselineVersion)

    def withBaselineDescription(baselineDescription: String): data.Fly4sConfig =
      i.copy(baselineDescription = baselineDescription)

    def withIgnoreMigrationPatterns(ignoreMigrationPatterns: List[ValidatePattern]): data.Fly4sConfig =
      i.copy(ignoreMigrationPatterns = ignoreMigrationPatterns)

    def withPlaceholders(placeholders: Map[String, String]): data.Fly4sConfig =
      i.copy(placeholders = placeholders)

    def withPlaceholderPrefix(placeholderPrefix: String): data.Fly4sConfig =
      i.copy(placeholderPrefix = placeholderPrefix)

    def withPlaceholderSuffix(placeholderSuffix: String): data.Fly4sConfig =
      i.copy(placeholderSuffix = placeholderSuffix)

    def withSqlMigrationPrefix(sqlMigrationPrefix: String): data.Fly4sConfig =
      i.copy(sqlMigrationPrefix = sqlMigrationPrefix)

    def withSqlMigrationSuffixes(sqlMigrationSuffixes: Seq[String]): data.Fly4sConfig =
      i.copy(sqlMigrationSuffixes = sqlMigrationSuffixes)

    def withRepeatableSqlMigrationPrefix(repeatableSqlMigrationPrefix: String): data.Fly4sConfig =
      i.copy(repeatableSqlMigrationPrefix = repeatableSqlMigrationPrefix)

    def withSqlMigrationSeparator(sqlMigrationSeparator: String): data.Fly4sConfig =
      i.copy(sqlMigrationSeparator = sqlMigrationSeparator)

    def withCallbacks(callbacks: List[Callback]): data.Fly4sConfig =
      i.copy(callbacks = callbacks)

    def withResolvers(resolvers: List[MigrationResolver]): data.Fly4sConfig =
      i.copy(resolvers = resolvers)

    def withResourceProvider(resourceProvider: Option[ResourceProvider]): data.Fly4sConfig =
      i.copy(resourceProvider = resourceProvider)

    def withGroup(group: Boolean): data.Fly4sConfig =
      i.copy(group = group)

    def withMixed(mixed: Boolean): data.Fly4sConfig =
      i.copy(mixed = mixed)

    def withFailOnMissingLocations(failOnMissingLocations: Boolean): data.Fly4sConfig =
      i.copy(failOnMissingLocations = failOnMissingLocations)

    def withValidateMigrationNaming(validateMigrationNaming: Boolean): data.Fly4sConfig =
      i.copy(validateMigrationNaming = validateMigrationNaming)

    def withValidateOnMigrate(validateOnMigrate: Boolean): data.Fly4sConfig =
      i.copy(validateOnMigrate = validateOnMigrate)

    def withCleanOnValidationError(cleanOnValidationError: Boolean): data.Fly4sConfig =
      i.copy(cleanOnValidationError = cleanOnValidationError)

    def withCleanDisabled(cleanDisabled: Boolean): data.Fly4sConfig =
      i.copy(cleanDisabled = cleanDisabled)

    def withCreateSchemas(createSchemas: Boolean): data.Fly4sConfig =
      i.copy(createSchemas = createSchemas)

    def withPlaceholderReplacement(placeholderReplacement: Boolean): data.Fly4sConfig =
      i.copy(placeholderReplacement = placeholderReplacement)

    def withBaselineOnMigrate(baselineOnMigrate: Boolean): data.Fly4sConfig =
      i.copy(baselineOnMigrate = baselineOnMigrate)

    def withOutOfOrder(outOfOrder: Boolean): data.Fly4sConfig =
      i.copy(outOfOrder = outOfOrder)

    def withSkipDefaultCallbacks(skipDefaultCallbacks: Boolean): data.Fly4sConfig =
      i.copy(skipDefaultCallbacks = skipDefaultCallbacks)

    def withSkipDefaultResolvers(skipDefaultResolvers: Boolean): Fly4sConfig =
      i.copy(skipDefaultResolvers = skipDefaultResolvers)
