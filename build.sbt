import sbt.project
import ModuleMdocPlugin.autoImport.mdocScalacOptions
import com.typesafe.tools.mima.core.{DirectMissingMethodProblem, ProblemFilters}

lazy val prjName                = "fly4s"
lazy val prjPackageName         = prjName.replaceAll("[^\\p{Alpha}\\d]+", ".")
lazy val prjDescription         = "A functional wrapper for Flywayy"
lazy val prjOrg                 = "com.github.geirolz"
lazy val scala213               = "2.13.16"
lazy val scala33                = "3.3.5"
lazy val supportedScalaVersions = List(scala213, scala33)

inThisBuild(
  List(
    organization := prjOrg,
    homepage     := Some(url(s"https://github.com/geirolz/$prjName")),
    licenses     := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "DavidGeirola",
        "David Geirola",
        "david.geirola@gmail.com",
        url("https://github.com/geirolz")
      )
    )
  )
)

//## global project to no publish ##
lazy val root: Project = project
  .in(file("."))
  .settings(baseSettings)
  .settings(noPublishSettings)
  .settings(
    crossScalaVersions := Nil
  )
  .aggregate(core)

lazy val core: Project =
  module("core")(
    folder             = "./core",
    publishAs          = Some(prjName),
    mimaCompatibleWith = Set("1.0.0")
  ).settings(
    mimaBinaryIssueFilters ++= Seq(
      ProblemFilters.exclude[DirectMissingMethodProblem]("fly4s.data.Fly4sConfig.apply"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("fly4s.data.Fly4sConfig.copy"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("fly4s.data.Fly4sConfig.this")
    ),
    libraryDependencies ++= {
      CrossVersion.partialVersion(Keys.scalaVersion.value) match {
        case Some((2, _)) => ProjectDependencies.for2_13_Only
        case _            => Nil
      }
    }
  )

//=============================== MODULES UTILS ===============================
def module(modName: String)(
  folder: String,
  publishAs: Option[String]       = None,
  mimaCompatibleWith: Set[String] = Set.empty
): Project = {
  val keys       = modName.split("-")
  val modDocName = keys.mkString(" ")
  val docNameStr = s"$prjName $modDocName"

  val publishSettings = publishAs match {
    case Some(pubName) =>
      Seq(
        moduleName     := pubName,
        publish / skip := false
      )
    case None => noPublishSettings
  }

  val mimaSettings = Seq(
    mimaFailOnNoPrevious := false,
    mimaPreviousArtifacts := mimaCompatibleWith.map { version =>
      organization.value % s"${moduleName.value}_${scalaBinaryVersion.value}" % version
    }
  )

  Project(modName, file(folder))
    .settings(
      name              := s"$prjName $modDocName",
      mdocIn            := file("docs"),
      mdocOut           := file("."),
      mdocScalacOptions := Seq("-Xsource:3"),
      mdocVariables := Map(
        "ORG"         -> prjOrg,
        "PRJ_NAME"    -> prjName,
        "DOCS_TITLE"  -> docNameStr.split(" ").map(_.capitalize).mkString(" "),
        "MODULE_NAME" -> moduleName.value,
        "VERSION"     -> previousStableVersion.value.getOrElse("<version>")
      ),
      mimaSettings,
      publishSettings,
      baseSettings
    )
    .enablePlugins(ModuleMdocPlugin)
}

def subProjectName(modPublishName: String): String = s"$prjName-$modPublishName"

//=============================== SETTINGS ===============================
lazy val noPublishSettings: Seq[Def.Setting[_]] = Seq(
  publish              := {},
  publishLocal         := {},
  publishArtifact      := false,
  publish / skip       := true,
  mimaFailOnNoPrevious := false
)

lazy val baseSettings: Seq[Def.Setting[_]] = Seq(
  // project
  name         := prjName,
  description  := prjDescription,
  organization := prjOrg,
  // scala
  crossScalaVersions := supportedScalaVersions,
  scalaVersion       := supportedScalaVersions.head,
  scalacOptions ++= scalacSettings(scalaVersion.value),
  versionScheme := Some("early-semver"),
  // dependencies
  resolvers ++= ProjectResolvers.all,
  libraryDependencies ++= ProjectDependencies.common ++ {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 13)) => ProjectDependencies.Plugins.compilerPluginsFor2_13
      case Some((3, _))  => ProjectDependencies.Plugins.compilerPluginsFor3
      case _             => Nil
    }
  },
  // fmt
  scalafmtOnCompile := true
)

def scalacSettings(scalaVersion: String): Seq[String] =
  Seq(
    //    "-Xlog-implicits",
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "utf-8", // Specify character encoding used by source files.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros", // Allow macro definition (besides implementation and application)
    "-language:higherKinds", // Allow higher-kinded types
    "-language:implicitConversions" // Allow definition of implicit functions called views
  ) ++ {
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((3, _)) =>
        Seq(
          "-Ykind-projector",
          "-explain-types", // Explain type errors in more detail.
          "-Xfatal-warnings" // Fail the compilation if there are any warnings.
        )
      case Some((2, 13)) =>
        Seq(
          "-explaintypes", // Explain type errors in more detail.
          "-unchecked", // Enable additional warnings where generated code depends on assumptions.
          "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
          "-Xfatal-warnings", // Fail the compilation if there are any warnings.
          "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
          "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
          "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
          "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
          "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
          "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
          "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
          "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
          "-Xlint:option-implicit", // Option.apply used implicit view.
          "-Xlint:package-object-classes", // Class or object defined in package object.
          "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
          "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
          "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
          "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
          "-Ywarn-dead-code", // Warn when dead code is identified.
          "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
          "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
          "-Ywarn-numeric-widen", // Warn when numerics are widened.
          "-Ywarn-value-discard", // Warn when non-Unit expression results are unused.
          "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
          "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
          "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
          "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
          "-Ywarn-unused:locals", // Warn if a local definition is unused.
          "-Ywarn-unused:explicits", // Warn if a explicit value parameter is unused.
          "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
          "-Ywarn-unused:privates", // Warn if a private member is unused.
          "-Ywarn-macros:after", // Tells the compiler to make the unused checks after macro expansion
          "-Xsource:3",
          "-P:kind-projector:underscore-placeholders",
          "-Ymacro-annotations"
        )
      case _ => Nil
    }
  }

//=============================== ALIASES ===============================
addCommandAlias("check", ";clean;test")
addCommandAlias(
  "gen-doc",
  List(
    core
  ).map(prj => s"project ${prj.id}-docs; mdoc").mkString(";") + s";project root;"
)
