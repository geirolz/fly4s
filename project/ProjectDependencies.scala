import sbt._

object ProjectDependencies {

  lazy val common: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core"   % "2.13.0",
    "org.typelevel" %% "cats-effect" % "3.6.3",
    "org.flywaydb"   % "flyway-core" % "11.18.0",
    // test
    "org.scalameta" %% "munit"             % "1.2.1"   % Test,
    "org.typelevel" %% "munit-cats-effect" % "2.1.0"   % Test,
    "com.h2database" % "h2"                % "2.4.240" % Test
  )

  lazy val for2_13_Only: Seq[ModuleID] = Seq(
    "com.github.geirolz" %% "fluent-copy" % "0.0.2"
  )

  object Plugins {
    val compilerPluginsFor2_13: Seq[ModuleID] = Seq(
      compilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.4" cross CrossVersion.full),
      compilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
    )

    val compilerPluginsFor3: Seq[ModuleID] = Nil
  }
}
