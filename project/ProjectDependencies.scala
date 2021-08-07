import sbt._

object ProjectDependencies {

  object Plugins {
    val compilerPluginsFor2_13: Seq[ModuleID] = Seq(
      compilerPlugin("org.typelevel" %% "kind-projector" % "0.13.0" cross CrossVersion.full),
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
      "org.typelevel" %% "cats-core" % "2.6.1",
      "org.typelevel" %% "cats-effect" % "3.2.2"
    )
  }

  private val tests: Seq[ModuleID] = Seq(
    "org.scalactic" %% "scalactic" % "3.2.9",
    "org.scalatest" %% "scalatest" % "3.2.9" % Test,
    "com.h2database" % "h2" % "1.4.200" % Test
  )

  private val db: Seq[ModuleID] = {
    Seq(
      "org.flywaydb" % "flyway-core" % "7.11.4"
    )
  }
}
