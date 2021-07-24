package fly4s.core.data

import cats.{Endo, MonoidK}
import cats.data.NonEmptyList
import org.flywaydb.core.api.configuration.{Configuration, FluentConfiguration}

import java.nio.charset.{Charset, StandardCharsets}
import scala.jdk.CollectionConverters.MapHasAsJava

case class Fly4sConfig(
  url: String,
  user: Option[String] = None,
  password: Option[Array[Char]] = None,
  connectRetries: Int = 0,
  initSql: Option[String] = None,
  defaultSchemaName: Option[String] = None,
  schemaNames: Option[NonEmptyList[String]] = None,
  //--- migrations ---
  installedBy: Option[String] = None,
  locations: List[Location],
  encoding: Charset = StandardCharsets.UTF_8,
  table: String = "flyway_schema_history",
  tablespace: Option[String] = None,
  targetVersion: MigrationVersion = MigrationVersion.latest,
  //--- placeholders ---
  placeholders: Map[String, String] = Map.empty,
  placeholderPrefix: String = "${",
  placeholderSuffix: String = "}",
  //--- flags ---
  group: Boolean = false,
  mixed: Boolean = false,
  ignoreMissingMigrations: Boolean = false,
  ignoreIgnoredMigrations: Boolean = false,
  ignorePendingMigrations: Boolean = false,
  ignoreFutureMigrations: Boolean = true,
  validateMigrationNaming: Boolean = false,
  validateOnMigrate: Boolean = true,
  cleanOnValidationError: Boolean = false,
  cleanDisabled: Boolean = false,
  createSchemas: Boolean = true,
  placeholderReplacement: Boolean = true,
  baselineOnMigrate: Boolean = false,
  outOfOrder: Boolean = false
) extends Fly4sFluentConfig {

  import cats.implicits._

  def toJava: Configuration = {

    def applyGenericConfig: Endo[FluentConfiguration] =
      _.dataSource(url, user.orNull, password.map(_.mkString).orNull)
        .connectRetries(connectRetries)
        .initSql(initSql.orNull)
        .defaultSchema(defaultSchemaName.orNull)
        .schemas(schemaNames.map(_.toList).getOrElse(Nil): _*)

    def applyMigrationsConf: Endo[FluentConfiguration] =
      _.locations(locations: _*)
        .installedBy(installedBy.orNull)
        .encoding(encoding)
        .table(table)
        .tablespace(tablespace.orNull)
        .target(targetVersion)
        //placeholders
        .placeholders(placeholders.asJava)
        .placeholderPrefix(placeholderPrefix)
        .placeholderSuffix(placeholderSuffix)

    def applyFlagsConf: Endo[FluentConfiguration] =
      _.group(group)
        .mixed(mixed)
        .ignoreMissingMigrations(ignoreMissingMigrations)
        .ignorePendingMigrations(ignorePendingMigrations)
        .ignorePendingMigrations(ignorePendingMigrations)
        .ignoreFutureMigrations(ignoreFutureMigrations)
        .validateMigrationNaming(validateMigrationNaming)
        .validateOnMigrate(validateOnMigrate)
        .cleanOnValidationError(cleanOnValidationError)
        .cleanDisabled(cleanDisabled)
        .createSchemas(createSchemas)
        .placeholderReplacement(placeholderReplacement)
        .baselineOnMigrate(baselineOnMigrate)
        .outOfOrder(outOfOrder)

    MonoidK[Endo]
      .algebra[FluentConfiguration]
      .combineAll(
        List(
          applyGenericConfig,
          applyMigrationsConf,
          applyFlagsConf
        )
      )
      .apply(new FluentConfiguration())
  }
}

sealed trait Fly4sFluentConfig extends Fly4sFluentFlagConfig { this: Fly4sConfig =>

  /** Sets the datasource to use. Must have the necessary privileges to execute DDL.
    *
    * @param url      The JDBC URL of the database.
    * @param user     The user of the database.
    * @param password The password of the database.
    */
  def dataSource(url: String, user: Option[String] = None, password: Option[Array[Char]] = None): Fly4sConfig =
    copy(url = url, user = user, password = password)

  /** The maximum number of retries when attempting to connect to the database. After each failed attempt, Flyway will
    * wait 1 second before attempting to connect again, up to the maximum number of times specified by connectRetries.
    *
    * @param connectRetries The maximum number of retries (default: `0`).
    */
  def connectRetries(connectRetries: Int): Fly4sConfig =
    copy(connectRetries = connectRetries)

  /** The SQL statements to run to initialize a new database connection immediately after opening it.
    *
    * @param initSql The SQL statements. (default: `None`)
    */
  def initSql(initSql: String): Fly4sConfig =
    copy(initSql = Option(initSql))

  /** Sets the default schema managed by Flyway. This schema name is case-sensitive. If not specified, but
    * <i>schemas</i> is, Flyway uses the first schema in that list. If that is also not specified, Flyway uses the default
    * schema for the database connection.
    * <p>Consequences:</p>
    * <ul>
    * <li>This schema will be the one containing the schema history table.</li>
    * <li>This schema will be the default for the database connection (provided the database supports this concept).</li>
    * </ul>
    *
    * @param defaultSchemaName The default schema managed by Flyway.
    */
  def defaultSchema(defaultSchemaName: String): Fly4sConfig =
    copy(defaultSchemaName = Option(defaultSchemaName))

  /** Sets the schemas managed by Flyway. These schema names are case-sensitive. If not specified, Flyway uses
    * the default schema for the database connection. If <i>defaultSchemaName</i> is not specified, then the first of
    * this list also acts as default schema.
    * <p>Consequences:</p>
    * <ul>
    * <li>Flyway will automatically attempt to create all these schemas, unless they already exist.</li>
    * <li>The schemas will be cleaned in the order of this list.</li>
    * <li>If Flyway created them, the schemas themselves will be dropped when cleaning.</li>
    * </ul>
    *
    * @param schemaNames The schemas managed by Flyway. May not be `null`. Must contain at least one element.
    */
  def schemas(schemaNames: NonEmptyList[String]): Fly4sConfig =
    copy(schemaNames = Option(schemaNames))

  //
  //  /** Sets the file name prefix for sql migrations.
  //    * SQL migrations have the following file name structure: prefixVERSIONseparatorDESCRIPTIONsuffix,
  //    * which using the defaults translates to V1_1__My_description.sql
  //    *
  //    * @param sqlMigrationPrefix The file name prefix for sql migrations (default: V)
  //    */
  //  def sqlMigrationPrefix(sqlMigrationPrefix: String): Fly4sConfig = {
  //    config.setSqlMigrationPrefix(sqlMigrationPrefix)
  //    this
  //  }
  //
  //  override def getUndoSqlMigrationPrefix: String = config.getUndoSqlMigrationPrefix
  //
  //  /** Sets the file name prefix for repeatable sql migrations.
  //    * Repeatable SQL migrations have the following file name structure: prefixSeparatorDESCRIPTIONsuffix,
  //    * which using the defaults translates to R__My_description.sql
  //    *
  //    * @param repeatableSqlMigrationPrefix The file name prefix for repeatable sql migrations (default: R)
  //    */
  //  def repeatableSqlMigrationPrefix(repeatableSqlMigrationPrefix: String): Fly4sConfig = {
  //    config.setRepeatableSqlMigrationPrefix(repeatableSqlMigrationPrefix)
  //    this
  //  }
  //
  //  /** Sets the file name separator for sql migrations.
  //    * SQL migrations have the following file name structure: prefixVERSIONseparatorDESCRIPTIONsuffix,
  //    * which using the defaults translates to V1_1__My_description.sql
  //    *
  //    * @param sqlMigrationSeparator The file name separator for sql migrations (default: __)
  //    */
  //  def sqlMigrationSeparator(sqlMigrationSeparator: String): Fly4sConfig = {
  //    config.setSqlMigrationSeparator(sqlMigrationSeparator)
  //    this
  //  }
  //
  //  /** The file name suffixes for SQL migrations. (default: .sql)
  //    * SQL migrations have the following file name structure: prefixVERSIONseparatorDESCRIPTIONsuffix,
  //    * which using the defaults translates to V1_1__My_description.sql
  //    * Multiple suffixes (like .sql,.pkg,.pkb) can be specified for easier compatibility with other tools such as
  //    * editors with specific file associations.
  //    *
  //    * @param sqlMigrationSuffixes The file name suffixes for SQL migrations.
  //    */
  //  def sqlMigrationSuffixes(sqlMigrationSuffixes: String*): Fly4sConfig = {
  //    config.setSqlMigrationSuffixes(sqlMigrationSuffixes)
  //    this
  //  }
  //
  //
  //
  //
  //
  //  /** Sets the version to tag an existing schema with when executing baseline.
  //    *
  //    * @param baselineVersion The version to tag an existing schema with when executing baseline. (default: 1)
  //    */
  //  def baselineVersion(baselineVersion: MigrationVersion): Fly4sConfig = {
  //    config.setBaselineVersion(baselineVersion)
  //    this
  //  }
  //
  //  def baselineVersion(baselineVersion: String): Fly4sConfig = {
  //    config.setBaselineVersion(MigrationVersion.fromVersion(baselineVersion))
  //    this
  //  }
  //
  //  /** Sets the description to tag an existing schema with when executing baseline.
  //    *
  //    * @param baselineDescription The description to tag an existing schema with when executing baseline. (default: &lt;&lt; Flyway Baseline &gt;&gt;)
  //    */
  //  def baselineDescription(baselineDescription: String): Fly4sConfig = {
  //    config.setBaselineDescription(baselineDescription)
  //    this
  //  }
  //
  //  override def isSkipDefaultCallbacks: Boolean = config.isSkipDefaultCallbacks
  //
  //  /** Set the callbacks for lifecycle notifications.
  //    *
  //    * @param callbacks The callbacks for lifecycle notifications. (default: none)
  //    */
  //  def callbacks(callbacks: Callback*): Fly4sConfig = {
  //    config.setCallbacks(callbacks)
  //    this
  //  }
  //
  //  /** Set the callbacks for lifecycle notifications.
  //    *
  //    * @param callbacks The fully qualified class names, or full qualified package to scan, of the callbacks for lifecycle notifications. (default: none)
  //    */
  //  def callbacks(callbacks: String*): Fly4sConfig = {
  //    config.setCallbacksAsClassNames(callbacks)
  //    this
  //  }
  //
  //  /** Whether Flyway should skip the default callbacks. If true, only custom callbacks are used.
  //    *
  //    * @param skipDefaultCallbacks Whether default built-in callbacks should be skipped. (default: false)
  //    */
  //  def skipDefaultCallbacks(skipDefaultCallbacks: Boolean): Fly4sConfig = {
  //    config.setSkipDefaultCallbacks(skipDefaultCallbacks)
  //    this
  //  }
  //
  //  /** Sets custom MigrationResolvers to be used in addition to the built-in ones for resolving Migrations to apply.
  //    *
  //    * @param resolvers The custom MigrationResolvers to be used in addition to the built-in ones for resolving Migrations to apply. (default: empty list)
  //    */
  //  def resolvers(resolvers: MigrationResolver*): Fly4sConfig = {
  //    config.setResolvers(resolvers)
  //    this
  //  }
  //
  //  /** Sets custom MigrationResolvers to be used in addition to the built-in ones for resolving Migrations to apply.
  //    *
  //    * @param resolvers The fully qualified class names of the custom MigrationResolvers to be used in addition to the built-in ones for resolving Migrations to apply. (default: empty list)
  //    */
  //  def resolvers(resolvers: String*): Fly4sConfig = {
  //    config.setResolversAsClassNames(resolvers)
  //    this
  //  }
  //
  //  /** Whether Flyway should skip the default resolvers. If true, only custom resolvers are used.
  //    *
  //    * @param skipDefaultResolvers Whether default built-in resolvers should be skipped. (default: false)
  //    */
  //  def skipDefaultResolvers(skipDefaultResolvers: Boolean): Fly4sConfig = {
  //    config.setSkipDefaultResolvers(skipDefaultResolvers)
  //    this
  //  }
  //
  //  def lockRetryCount(lockRetryCount: Int): Fly4sConfig = {
  //    config.setLockRetryCount(lockRetryCount)
  //    this
  //  }
  //
  //  /** Custom ResourceProvider to be used to look up resources. If not set, the default strategy will be used.
  //    *
  //    * @param resourceProvider Custom ResourceProvider to be used to look up resources
  //    */
  //  def resourceProvider(resourceProvider: ResourceProvider): Fly4sConfig = {
  //    config.setResourceProvider(resourceProvider)
  //    this
  //  }
  //
  //  /** Custom ClassProvider to be used to look up {@link JavaMigration} classes. If not set, the default strategy will be used.
  //    *
  //    * @param javaMigrationClassProvider Custom ClassProvider to be used to look up {@link JavaMigration} classes.
  //    */
  //  def javaMigrationClassProvider(javaMigrationClassProvider: ClassProvider[JavaMigration]): Fly4sConfig = {
  //    config.setJavaMigrationClassProvider(javaMigrationClassProvider)
  //    this
  //  }
  //
  //  /** Configures Flyway with these properties. This overwrites any existing configuration. Property names are documented in the flyway maven plugin.
  //    * To use a custom ClassLoader, setClassLoader() must be called prior to calling this method.
  //    *
  //    * @param properties Properties used for configuration.
  //    * @throws FlywayException when the configuration failed.
  //    */
  //  def configuration(properties: Properties): Fly4sConfig = {
  //    config.configure(properties)
  //    this
  //  }
  //
  //  /** Configures Flyway with these properties. This overwrites any existing configuration. Property names are documented in the flyway maven plugin.
  //    * To use a custom ClassLoader, it must be passed to the Flyway constructor prior to calling this method.
  //    *
  //    * @param props Properties used for configuration.
  //    * @throws FlywayException when the configuration failed.
  //    */
  //  def configuration(props: util.Map[String, String]): Fly4sConfig = {
  //    config.configure(props)
  //    this
  //  }
  //
  //  /** Load configuration files from the default locations:
  //    * $installationDir$/conf/flyway.conf
  //    * $user.home$/flyway.conf
  //    * $workingDirectory$/flyway.conf
  //    *
  //    * The configuration files must be encoded with UTF-8.
  //    *
  //    * @throws FlywayException When the configuration failed.
  //    */
  //  def loadDefaultConfigurationFiles: Fly4sConfig = loadDefaultConfigurationFiles("UTF-8")
  //
  //  /** Load configuration files from the default locations:
  //    * $installationDir$/conf/flyway.conf
  //    * $user.home$/flyway.conf
  //    * $workingDirectory$/flyway.conf
  //    *
  //    * @param encoding The conf file encoding.
  //    * @throws FlywayException When the configuration failed.
  //    */
  //  def loadDefaultConfigurationFiles(encoding: String): Fly4sConfig = {
  //    val installationPath: String = ClassUtils.getLocationOnDisk(classOf[Fly4sConfig])
  //    val installationDir: File = new File(installationPath).getParentFile
  //    val configMap: util.Map[String, String] = ConfigUtils.loadDefaultConfigurationFiles(installationDir, encoding)
  //    config.configure(configMap)
  //    this
  //  }
  //
  //
  //  /** Configures Flyway using FLYWAY_* environment variables.
  //    *
  //    * @throws FlywayException When the configuration failed.
  //    */
  //  def envVars: Fly4sConfig = {
  //    config.configureUsingEnvVars()
  //    this
  //  }
  //
  //  /** Whether to fail if a location specified in the flyway.locations option doesn't exist
  //    *
  //    * @return @{code true} to fail (default: `false`)
  //    */
  //  def failOnMissingLocations(failOnMissingLocations: Boolean): Fly4sConfig = {
  //    config.setFailOnMissingLocations(failOnMissingLocations)
  //    this
  //  }
}

sealed trait Fly4sFluentMigrationConfig { this: Fly4sConfig =>

  /** The username that will be recorded in the schema history table as having applied the migration.
    *
    * @param installedBy The username or `None` for the current database user of the connection. (default: `None`).
    */
  def installedBy(installedBy: String): Fly4sConfig =
    copy(installedBy = Option(installedBy))

  /** Sets the locations to scan recursively for migrations.
    * The location type is determined by its prefix.
    * Unprefixed locations or locations starting with `classpath:` point to a package on the classpath and may
    * contain both SQL and Java-based migrations.
    * Locations starting with `filesystem:` point to a directory on the filesystem, may only
    * contain SQL migrations and are only scanned recursively down non-hidden directories.
    *
    * @param migrationsLocations Locations to scan recursively for migrations. (default: db/migration)
    */
  def locations(migrationsLocations: Location*): Fly4sConfig =
    copy(locations = migrationsLocations.toList)

  /** Sets the encoding of SQL migrations.
    *
    * @param encoding The encoding of SQL migrations. (default: StandardCharsets.UTF-8)
    */
  def encoding(encoding: Charset): Fly4sConfig =
    copy(encoding = encoding)

  /** Sets the name of the schema history table that will be used by Flyway.
    * By default (single-schema mode) the schema history table is placed in the default schema for the connection
    * provided by the datasource. When the <i>flyway.schemas</i> property is set (multi-schema mode), the schema
    * history table is placed in the first schema of the list.
    *
    * @param table The name of the schema history table that will be used by Flyway. (default: flyway_schema_history)
    */
  def table(table: String): Fly4sConfig =
    copy(table = table)

  /** Sets the tablespace where to create the schema history table that will be used by Flyway.
    * If not specified, Flyway uses the default tablespace for the database connection.
    * This setting is only relevant for databases that do support the notion of tablespaces. Its value is simply ignored for all others.
    *
    * @param tablespace The tablespace where to create the schema history table that will be used by Flyway.
    */
  def tablespace(tablespace: String): Fly4sConfig =
    copy(tablespace = Option(tablespace))

  /** Sets the target version up to which Flyway should consider migrations.
    * Migrations with a higher version number will be ignored.
    * Special values:
    * <ul>
    * <li>`current`: Designates the current version of the schema</li>
    * <li>`latest`: The latest version of the schema, as defined by the migration with the highest version</li>
    * </ul>
    * Defaults to `None` that means `latest`.
    */
  def targetVersion(targetVersion: MigrationVersion): Fly4sConfig =
    copy(targetVersion = targetVersion)

  /** Sets the placeholders to replace in sql migration scripts.
    *
    * @param placeholders The map of &lt;placeholder, replacementValue&gt; to apply to sql migration scripts.
    */
  def placeholders(placeholders: Map[String, String]): Fly4sConfig =
    copy(placeholders = placeholders)

  /** Sets the prefix of every placeholder.
    *
    * @param placeholderPrefix The prefix of every placeholder. (default: `${` )
    */
  def placeholderPrefix(placeholderPrefix: String): Fly4sConfig =
    copy(placeholderPrefix = placeholderPrefix)

  /** Sets the suffix of every placeholder.
    *
    * @param placeholderSuffix The suffix of every placeholder. (default: `}` )
    */
  def placeholderSuffix(placeholderSuffix: String): Fly4sConfig =
    copy(placeholderSuffix = placeholderSuffix)
}

sealed trait Fly4sFluentFlagConfig { this: Fly4sConfig =>

  /** Whether to group all pending migrations together in the same transaction when applying them (only recommended for databases with support for DDL transactions).
    *
    * @param group `true` if migrations should be grouped. `false` if they should be applied individually instead. (default: `false`)
    */
  def group(group: Boolean): Fly4sConfig =
    copy(group = group)

  /** Whether to allow mixing transactional and non-transactional statements within the same migration. Enabling this
    * automatically causes the entire affected migration to be run without a transaction.
    *
    * Note that this is only applicable for PostgreSQL, Aurora PostgreSQL, SQL Server and SQLite which all have
    * statements that do not run at all within a transaction.
    * This is not to be confused with implicit transaction, as they occur in MySQL or Oracle, where even though a
    * DDL statement was run within a transaction, the database will issue an implicit commit before and after
    * its execution.
    *
    * @param mixed `true` if mixed migrations should be allowed. `false` if an error should be thrown instead. (default: `false`)
    */
  def mixed(mixed: Boolean): Fly4sConfig =
    copy(mixed = mixed)

  /** Ignore missing migrations when reading the schema history table. These are migrations that were performed by an
    * older deployment of the application that are no longer available in this version. For example: we have migrations
    * available on the classpath with versions 1.0 and 3.0. The schema history table indicates that a migration with version 2.0
    * (unknown to us) has also been applied. Instead of bombing out (fail fast) with an exception, a
    * warning is logged and Flyway continues normally. This is useful for situations where one must be able to deploy
    * a newer version of the application even though it doesn't contain migrations included with an older one anymore.
    * Note that if the most recently applied migration is removed, Flyway has no way to know it is missing and will
    * mark it as future instead.
    *
    * @param ignoreMissingMigrations `true` to continue normally and log a warning, `false` to fail fast with an exception. (default: `false`)
    */
  def ignoreMissingMigrations(ignoreMissingMigrations: Boolean): Fly4sConfig =
    copy(ignoreMissingMigrations = ignoreMissingMigrations)

  /** Ignore ignored migrations when reading the schema history table. These are migrations that were added in between
    * already migrated migrations in this version. For example: we have migrations available on the classpath with
    * versions from 1.0 to 3.0. The schema history table indicates that version 1 was finished on 1.0.15, and the next
    * one was 2.0.0. But with the next release a new migration was added to version 1: 1.0.16. Such scenario is ignored
    * by migrate command, but by default is rejected by validate. When ignoreIgnoredMigrations is enabled, such case
    * will not be reported by validate command. This is useful for situations where one must be able to deliver
    * complete set of migrations in a delivery package for multiple versions of the product, and allows for further
    * development of older versions.
    *
    * @param ignoreIgnoredMigrations `true` to continue normally, `false` to fail fast with an exception. (default: `false`)
    */
  def ignoreIgnoredMigrations(ignoreIgnoredMigrations: Boolean): Fly4sConfig =
    copy(ignoreIgnoredMigrations = ignoreIgnoredMigrations)

  /** Ignore pending migrations when reading the schema history table. These are migrations that are available
    * but have not yet been applied. This can be useful for verifying that in-development migration changes
    * don't contain any validation-breaking changes of migrations that have already been applied to a production
    * environment, e.g. as part of a CI/CD process, without failing because of the existence of new migration versions.
    *
    * @param ignorePendingMigrations `true` to continue normally, `false` to fail fast with an exception. (default: `false`)
    */
  def ignorePendingMigrations(ignorePendingMigrations: Boolean): Fly4sConfig =
    copy(ignorePendingMigrations = ignorePendingMigrations)

  /** Whether to ignore future migrations when reading the schema history table. These are migrations that were performed by a
    * newer deployment of the application that are not yet available in this version. For example: we have migrations
    * available on the classpath up to version 3.0. The schema history table indicates that a migration to version 4.0
    * (unknown to us) has already been applied. Instead of bombing out (fail fast) with an exception, a
    * warning is logged and Flyway continues normally. This is useful for situations where one must be able to redeploy
    * an older version of the application after the database has been migrated by a newer one.
    *
    * @param ignoreFutureMigrations `true` to continue normally and log a warning, `false` to fail fast with an exception. (default: `true`)
    */
  def ignoreFutureMigrations(ignoreFutureMigrations: Boolean): Fly4sConfig =
    copy(ignoreFutureMigrations = ignoreFutureMigrations)

  /** Whether to validate migrations and callbacks whose scripts do not obey the correct naming convention. A failure can be
    * useful to check that errors such as case sensitivity in migration prefixes have been corrected.
    *
    * @param validateMigrationNaming `false` to continue normally, `true` to fail fast with an exception. (default: `false`)
    */
  def validateMigrationNaming(validateMigrationNaming: Boolean): Fly4sConfig =
    copy(validateMigrationNaming = validateMigrationNaming)

  /** Whether to automatically call validate or not when running migrate.
    *
    * @param validateOnMigrate `true` if validate should be called. `false` if not. (default: `true`)
    */
  def validateOnMigrate(validateOnMigrate: Boolean): Fly4sConfig =
    copy(validateOnMigrate = validateOnMigrate)

  /** Whether to automatically call clean or not when a validation error occurs.
    * This is exclusively intended as a convenience for development. even though we strongly recommend not to change
    * migration scripts once they have been checked into SCM and run, this provides a way of dealing with this case in
    * a smooth manner. The database will be wiped clean automatically, ensuring that the next migration will bring you
    * back to the state checked into SCM.
    * <b>Warning! Do not enable in production!</b>
    *
    * @param cleanOnValidationError `true` if clean should be called. `false` if not. (default: `false`)
    */
  def cleanOnValidationError(cleanOnValidationError: Boolean): Fly4sConfig =
    copy(cleanOnValidationError = cleanOnValidationError)

  /** Whether to disable clean.
    * This is especially useful for production environments where running clean can be quite a career limiting move.
    *
    * @param cleanDisabled `true` to disable clean. `false` to leave it enabled.  (default: `false`)
    */
  def cleanDisabled(cleanDisabled: Boolean): Fly4sConfig =
    copy(cleanDisabled = cleanDisabled)

  /** Whether Flyway should attempt to create the schemas specified in the schemas property
    *
    * @param createSchemas @{code true} to attempt to create the schemas (default: `true`)
    */
  def createSchemas(createSchemas: Boolean): Fly4sConfig =
    copy(createSchemas = createSchemas)

  /** Sets whether placeholders should be replaced.
    *
    * @param placeholderReplacement Whether placeholders should be replaced. (default: true)
    */
  def placeholderReplacement(placeholderReplacement: Boolean): Fly4sConfig =
    copy(placeholderReplacement = placeholderReplacement)

  /** Whether to automatically call baseline when migrate is executed against a non-empty schema with no schema history table.
    * This schema will then be baselined with the `baselineVersion` before executing the migrations.
    * Only migrations above `baselineVersion` will then be applied.
    *
    * This is useful for initial Flyway production deployments on projects with an existing DB.
    *
    * Be careful when enabling this as it removes the safety net that ensures
    * Flyway does not migrate the wrong database in case of a configuration mistake!
    *
    * @param baselineOnMigrate `true` if baseline should be called on migrate for non-empty schemas, `false` if not. (default: `false`)
    */
  def baselineOnMigrate(baselineOnMigrate: Boolean): Fly4sConfig =
    copy(baselineOnMigrate = baselineOnMigrate)

  /** Allows migrations to be run "out of order".
    * If you already have versions 1 and 3 applied, and now a version 2 is found, it will be applied too instead of being ignored.
    *
    * @param outOfOrder `true` if outOfOrder migrations should be applied, `false` if not. (default: `false`)
    */
  def outOfOrder(outOfOrder: Boolean): Fly4sConfig =
    copy(outOfOrder = outOfOrder)
}
