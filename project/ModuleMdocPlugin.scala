import mdoc.MdocPlugin
import mdoc.MdocPlugin.autoImport._
import sbt.{settingKey, Def, _}
import sbt.Keys._

/** This is needed because unlike tut there is no separate classpath configuration for
  * documentation. See https://github.com/scalameta/mdoc/issues/155 for details.
  */
object ModuleMdocPlugin extends AutoPlugin {

  object autoImport {
    val mdocLibraryDependencies: SettingKey[Seq[sbt.ModuleID]] =
      settingKey[Seq[ModuleID]]("Declares managed dependencies for the mdoc project.")
    val mdocScalacOptions: SettingKey[Seq[String]] =
      settingKey[Seq[String]]("Options for the Scala compiler in the mdoc project.")
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    mdocIn  := baseDirectory.value / "docs",
    mdocOut := baseDirectory.value,
//    mdocLibraryDependencies := Nil,
    mdocScalacOptions := Nil
  )

  override def derivedProjects(proj: ProjectDefinition[_]): Seq[Project] = {
    val moduleProj  = LocalProject(proj.id)
    val docProjId   = s"${proj.id}-docs"
    val docProjRoot = proj.base / "target" / "docs-project"

    val docProj =
      Project(docProjId, docProjRoot)
        .enablePlugins(MdocPlugin)
        .dependsOn(moduleProj)
        .settings(
          name    := docProjId,
          mdocIn  := (moduleProj / mdocIn).value,
          mdocOut := (moduleProj / mdocOut).value,
//          mdocExtraArguments += "--no-link-hygiene",
          mdocVariables := (moduleProj / mdocVariables).value,
          libraryDependencies ++= (moduleProj / libraryDependencies).value,
          scalacOptions  := (moduleProj / mdocScalacOptions).value,
          scalaVersion   := (moduleProj / scalaVersion).value,
          publish / skip := true
        )

    List(docProj)
  }
}
