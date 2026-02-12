import sbt._

object ProjectDependencies {

  private val catsVersion             = "2.13.0"
  private val catsEffectVersion       = "3.6.3"
  private val flywayVersion           = "12.0.0"
  private val munitVersion            = "1.2.2"
  private val munitCatsEffectVersion  = "2.1.0"
  private val h2Version               = "2.4.240"
  private val fluentCopyVersion       = "0.0.2"
  private val kindProjectorVersion    = "0.13.4"
  private val betterMonadicForVersion = "0.3.1"

  lazy val common: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core"   % catsVersion,
    "org.typelevel" %% "cats-effect" % catsEffectVersion,
    "org.flywaydb"   % "flyway-core" % flywayVersion,
    // test
    "org.scalameta" %% "munit"             % munitVersion           % Test,
    "org.typelevel" %% "munit-cats-effect" % munitCatsEffectVersion % Test,
    "com.h2database" % "h2"                % h2Version              % Test
  )

  lazy val for2_13_Only: Seq[ModuleID] = Seq(
    "com.github.geirolz" %% "fluent-copy" % fluentCopyVersion
  )

  object Plugins {
    val compilerPluginsFor2_13: Seq[ModuleID] = Seq(
      compilerPlugin(
        "org.typelevel" %% "kind-projector" % kindProjectorVersion cross CrossVersion.full
      ),
      compilerPlugin("com.olegpy" %% "better-monadic-for" % betterMonadicForVersion)
    )

    val compilerPluginsFor3: Seq[ModuleID] = Nil
  }
}
