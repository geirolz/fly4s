package fly4s.core.data

import cats.data.NonEmptyList
import com.geirolz.macros.fluentcopy.FluentCopyMacros.GenerateFluentCopy
import org.flywaydb.core.api.configuration.{Configuration, FluentConfiguration}

import java.nio.charset.{Charset, StandardCharsets}
import scala.jdk.CollectionConverters.{MapHasAsJava, MapHasAsScala}
import scala.util.Try

@GenerateFluentCopy
case class Fly4sConfig(
  connectRetries: Int                       = 0,
  initSql: Option[String]                   = None,
  defaultSchemaName: Option[String]         = None,
  schemaNames: Option[NonEmptyList[String]] = None,
  lockRetryCount: Int                       = 50,
  // --- migrations ---
  installedBy: Option[String]                    = None,
  locations: List[Location]                      = List(Location("db/migration")),
  encoding: Charset                              = StandardCharsets.UTF_8,
  table: String                                  = "flyway_schema_history",
  tablespace: Option[String]                     = None,
  targetVersion: MigrationVersion                = MigrationVersion.latest,
  baselineVersion: MigrationVersion              = MigrationVersion.one,
  baselineDescription: String                    = "<< Flyway Baseline >>",
  ignoreMigrationPatterns: List[ValidatePattern] = Nil,
  // --- placeholders ---
  placeholders: Map[String, String] = Map.empty,
  placeholderPrefix: String         = "${",
  placeholderSuffix: String         = "}",
  // --- migrations naming ---
  sqlMigrationPrefix: String           = "V",
  sqlMigrationSuffixes: Seq[String]    = Seq(".sql"),
  repeatableSqlMigrationPrefix: String = "R",
  sqlMigrationSeparator: String        = "__",
  // --- migrations functions ---
  callbacks: List[Callback]                  = Nil,
  resolvers: List[MigrationResolver]         = Nil,
  resourceProvider: Option[ResourceProvider] = None,
  // --- flags ---
  group: Boolean                   = false,
  mixed: Boolean                   = false,
  failOnMissingLocations: Boolean  = false,
  validateMigrationNaming: Boolean = false,
  validateOnMigrate: Boolean       = true,
  cleanOnValidationError: Boolean  = false,
  cleanDisabled: Boolean           = false,
  createSchemas: Boolean           = true,
  placeholderReplacement: Boolean  = true,
  baselineOnMigrate: Boolean       = false,
  outOfOrder: Boolean              = false,
  skipDefaultCallbacks: Boolean    = false,
  skipDefaultResolvers: Boolean    = false
)

object Fly4sConfig {

  lazy val default: Fly4sConfig = Fly4sConfig()

  def fromJava(c: Configuration): Fly4sConfig =
    Fly4sConfig(
      // ---------- connection ----------
      connectRetries    = c.getConnectRetries,
      initSql           = Option(c.getInitSql),
      defaultSchemaName = Option(c.getDefaultSchema),
      schemaNames       = NonEmptyList.fromList(c.getSchemas.toList),
      lockRetryCount    = c.getLockRetryCount,
      // ---------- migrations ----------
      locations               = c.getLocations.toList,
      installedBy             = Option(c.getInstalledBy),
      encoding                = c.getEncoding,
      table                   = c.getTable,
      tablespace              = Option(c.getTablespace),
      targetVersion           = c.getTarget,
      baselineVersion         = c.getBaselineVersion,
      baselineDescription     = c.getBaselineDescription,
      ignoreMigrationPatterns = c.getIgnoreMigrationPatterns.toList,
      // migrations - placeholders
      placeholders      = c.getPlaceholders.asScala.toMap,
      placeholderPrefix = c.getPlaceholderPrefix,
      placeholderSuffix = c.getPlaceholderSuffix,
      // migrations - naming
      sqlMigrationPrefix           = c.getSqlMigrationPrefix,
      sqlMigrationSuffixes         = c.getSqlMigrationSuffixes.toList,
      repeatableSqlMigrationPrefix = c.getRepeatableSqlMigrationPrefix,
      sqlMigrationSeparator        = c.getSqlMigrationSeparator,
      // migrations - functions
      callbacks        = c.getCallbacks.toList,
      resolvers        = c.getResolvers.toList,
      resourceProvider = Option(c.getResourceProvider),
      // ---------- flags ----------
      group                   = c.isGroup,
      mixed                   = c.isMixed,
      failOnMissingLocations  = c.isFailOnMissingLocations,
      validateMigrationNaming = c.isValidateMigrationNaming,
      validateOnMigrate       = c.isValidateOnMigrate,
      cleanOnValidationError  = c.isCleanOnValidationError,
      cleanDisabled           = c.isCleanDisabled,
      createSchemas           = c.isCreateSchemas,
      placeholderReplacement  = c.isPlaceholderReplacement,
      baselineOnMigrate       = c.isBaselineOnMigrate,
      outOfOrder              = c.isOutOfOrder,
      skipDefaultCallbacks    = c.isSkipDefaultCallbacks,
      skipDefaultResolvers    = c.isSkipDefaultResolvers
    )

  def toJava(
    c: Fly4sConfig,
    classLoader: ClassLoader = Thread.currentThread.getContextClassLoader
  ): Try[Configuration] = Try {
    // ---------- connection ----------
    new FluentConfiguration(classLoader)
      .connectRetries(c.connectRetries)
      .initSql(c.initSql.orNull)
      .defaultSchema(c.defaultSchemaName.orNull)
      .schemas(c.schemaNames.map(_.toList).getOrElse(Nil)*)
      .lockRetryCount(c.lockRetryCount)

      // ---------- migrations ----------
      .locations(c.locations*)
      .installedBy(c.installedBy.orNull)
      .encoding(c.encoding)
      .table(c.table)
      .tablespace(c.tablespace.orNull)
      .target(c.targetVersion)
      .baselineVersion(c.baselineVersion)
      .baselineDescription(c.baselineDescription)
      .ignoreMigrationPatterns(c.ignoreMigrationPatterns*)
      // placeholders
      .placeholders(c.placeholders.asJava)
      .placeholderPrefix(c.placeholderPrefix)
      .placeholderSuffix(c.placeholderSuffix)
      // migrations naming
      .sqlMigrationPrefix(c.sqlMigrationPrefix)
      .sqlMigrationSuffixes(c.sqlMigrationSuffixes*)
      .repeatableSqlMigrationPrefix(c.repeatableSqlMigrationPrefix)
      .sqlMigrationSeparator(c.sqlMigrationSeparator)
      // migrations - functions
      .callbacks(c.callbacks*)
      .resolvers(c.resolvers*)
      .resourceProvider(c.resourceProvider.orNull)
      // ---------- flags ----------
      .group(c.group)
      .mixed(c.mixed)
      .failOnMissingLocations(c.failOnMissingLocations)
      .validateMigrationNaming(c.validateMigrationNaming)
      .validateOnMigrate(c.validateOnMigrate)
      .cleanOnValidationError(c.cleanOnValidationError)
      .cleanDisabled(c.cleanDisabled)
      .createSchemas(c.createSchemas)
      .placeholderReplacement(c.placeholderReplacement)
      .baselineOnMigrate(c.baselineOnMigrate)
      .outOfOrder(c.outOfOrder)
      .skipDefaultCallbacks(c.skipDefaultCallbacks)
      .skipDefaultResolvers(c.skipDefaultResolvers)
  }
}
