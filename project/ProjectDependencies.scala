import sbt._

object ProjectDependencies {

  lazy val common: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core" % "2.9.0",
    "org.typelevel" %% "cats-effect" % "3.3.14",
    "org.flywaydb" % "flyway-core" % "9.1.5",

    // test
    "org.scalameta" %% "munit" % "0.7.29" % Test,
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test
  )

  object Plugins {
    val compilerPluginsFor2_13: Seq[ModuleID] = Seq(
      compilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full),
      compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
    )

    val compilerPluginsFor3: Seq[ModuleID] = Nil
  }

  object Core {
    lazy val dedicated: Seq[ModuleID] = Seq(
      "com.h2database" % "h2" % "2.1.214" % Test
    )
  }

  object Macros {
    lazy val dedicated: Seq[ModuleID] = Seq(
      "org.scala-lang" % "scala-reflect" % "2.13.10"
    )
  }
}
