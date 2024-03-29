import sbt._

object ProjectDependencies {

  lazy val common: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core"   % "2.10.0",
    "org.typelevel" %% "cats-effect" % "3.5.4",
    "org.flywaydb"   % "flyway-core" % "10.10.0",
    // test
    "org.scalameta" %% "munit"               % "0.7.29"  % Test,
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.7"   % Test,
    "com.h2database" % "h2"                  % "2.2.224" % Test
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
