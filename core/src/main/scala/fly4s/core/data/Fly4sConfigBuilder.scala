package fly4s.core.data

import cats.data.NonEmptyList
import org.flywaydb.core.api.configuration.{Configuration, FluentConfiguration}

import java.nio.charset.{Charset, StandardCharsets}
import scala.jdk.CollectionConverters.{MapHasAsJava, MapHasAsScala}
import scala.util.Try

private[fly4s] trait Fly4sConfigDefaults {
  val connectRetries: Int
  val initSql: Option[String]
  val defaultSchemaName: Option[String]
  val schemaNames: Option[NonEmptyList[String]]
  val lockRetryCount: Int

  // --- migrations ---
  val installedBy: Option[String]
  val locations: List[Location]
  val encoding: Charset
  val table: String
  val tablespace: Option[String]
  val targetVersion: MigrationVersion
  val baselineVersion: MigrationVersion
  val baselineDescription: String
  val ignoreMigrationPatterns: List[ValidatePattern]

  // --- placeholders ---
  val placeholders: Map[String, String]
  val placeholderPrefix: String
  val placeholderSuffix: String

  // --- migrations naming ---
  val sqlMigrationPrefix: String
  val sqlMigrationSuffixes: Seq[String]
  val repeatableSqlMigrationPrefix: String
  val sqlMigrationSeparator: String

  // --- migrations functions ---
  val callbacks: List[Callback]
  val resolvers: List[MigrationResolver]
  val resourceProvider: Option[ResourceProvider]

  // --- flags ---
  val group: Boolean
  val mixed: Boolean
  val failOnMissingLocations: Boolean
  val validateMigrationNaming: Boolean
  val validateOnMigrate: Boolean
  val cleanOnValidationError: Boolean
  val cleanDisabled: Boolean
  val createSchemas: Boolean
  val placeholderReplacement: Boolean
  val baselineOnMigrate: Boolean
  val outOfOrder: Boolean
  val skipDefaultCallbacks: Boolean
  val skipDefaultResolvers: Boolean
}
private[fly4s] object Fly4sConfigDefaults {
  val defaultConnectRetries: Int                       = 0
  val defaultInitSql: Option[String]                   = None
  val defaultDefaultSchemaName: Option[String]         = None
  val defaultSchemaNames: Option[NonEmptyList[String]] = None
  val defaultLockRetryCount: Int                       = 50

  // --- migrations ---
  val defaultInstalledBy: Option[String]                    = None
  val defaultLocations: List[Location]                      = List(Location("db/migration"))
  val defaultEncoding: Charset                              = StandardCharsets.UTF_8
  val defaultTable: String                                  = "flyway_schema_history"
  val defaultTablespace: Option[String]                     = None
  val defaultTargetVersion: MigrationVersion                = MigrationVersion.latest
  val defaultBaselineVersion: MigrationVersion              = MigrationVersion.one
  val defaultBaselineDescription: String                    = "<< Flyway Baseline >>"
  val defaultIgnoreMigrationPatterns: List[ValidatePattern] = Nil

  // --- placeholders ---
  val defaultPlaceholders: Map[String, String] = Map.empty
  val defaultPlaceholderPrefix: String         = "${"
  val defaultPlaceholderSuffix: String         = "}"

  // --- migrations naming ---
  val defaultSqlMigrationPrefix: String           = "V"
  val defaultSqlMigrationSuffixes: Seq[String]    = Seq(".sql")
  val defaultRepeatableSqlMigrationPrefix: String = "R"
  val defaultSqlMigrationSeparator: String        = "__"

  // --- migrations functions ---
  val defaultCallbacks: List[Callback]                  = Nil
  val defaultResolvers: List[MigrationResolver]         = Nil
  val defaultResourceProvider: Option[ResourceProvider] = None

  // --- flags ---
  val defaultGroup: Boolean                   = false
  val defaultMixed: Boolean                   = false
  val defaultFailOnMissingLocations: Boolean  = false
  val defaultValidateMigrationNaming: Boolean = false
  val defaultValidateOnMigrate: Boolean       = true
  val defaultCleanOnValidationError: Boolean  = false
  val defaultCleanDisabled: Boolean           = true
  val defaultCreateSchemas: Boolean           = true
  val defaultPlaceholderReplacement: Boolean  = true
  val defaultBaselineOnMigrate: Boolean       = false
  val defaultOutOfOrder: Boolean              = false
  val defaultSkipDefaultCallbacks: Boolean    = false
  val defaultSkipDefaultResolvers: Boolean    = false
}

private[fly4s] trait Fly4sConfigBuilder {

  lazy val default: Fly4sConfig = Fly4sConfig()

  def fromJava(c: Configuration): Fly4sConfig =
    new Fly4sConfig(
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
