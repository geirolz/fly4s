import sbt._

object ProjectDependencies {

  lazy val common: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core"   % "2.12.0",
    "org.typelevel" %% "cats-effect" % "3.5.7",
    "org.flywaydb"   % "flyway-core" % "11.0.1",
    // test
    "org.scalameta" %% "munit"             % "1.0.3"   % Test,
    "org.typelevel" %% "munit-cats-effect" % "2.0.0"   % Test,
    "com.h2database" % "h2"                % "2.3.232" % Test
  )

  lazy val for2_13_Only: Seq[ModuleID] = Seq(
    "com.github.geirolz" %% "fluent-copy" % "0.0.2"
  )

  object Plugins {
    val compilerPluginsFor2_13: Seq[ModuleID] = Seq(
      compilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.3" cross CrossVersion.full),
      compilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
    )

    val compilerPluginsFor3: Seq[ModuleID] = Nil
  }
}
