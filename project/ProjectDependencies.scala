import sbt._

object ProjectDependencies {

  object Plugins {
    val compilerPluginsFor2_13: Seq[ModuleID] = Seq(
      compilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full),
      compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
    )

    val compilerPluginsFor3: Seq[ModuleID] = Nil
  }

  lazy val common: Seq[ModuleID] = Seq(
    effects,
    tests,
    db
  ).flatten

  private val effects: Seq[ModuleID] = {
    Seq(
      "org.typelevel" %% "cats-core" % "2.8.0",
      "org.typelevel" %% "cats-effect" % "3.3.14"
    )
  }

  private val tests: Seq[ModuleID] = Seq(
    "org.scalactic" %% "scalactic" % "3.2.12",
    "org.scalatest" %% "scalatest" % "3.2.12" % Test,
    "com.h2database" % "h2" % "2.1.214" % Test,
    "org.typelevel" %% "cats-effect-testing-scalatest" % "1.4.0" % Test
  )

  private val db: Seq[ModuleID] = {
    Seq(
      "org.flywaydb" % "flyway-core" % "8.5.13"
    )
  }
}
