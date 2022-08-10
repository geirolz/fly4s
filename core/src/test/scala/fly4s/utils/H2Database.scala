package fly4s.utils

import cats.effect.{IO, Resource}

import java.io.File as JFile
import java.sql.{Connection, DriverManager}

class H2Database(
  val h2Settings: H2Settings,
  val h2Connection: Connection
) {

  def executeUpdate(sql: String): IO[Int] =
    IO(h2Connection.createStatement.executeUpdate(sql))

  def dropAllTables: IO[Int] =
    executeUpdate("DROP ALL OBJECT")
}
object H2Database {

  import cats.implicits.*

  def make(h2Settings: H2Settings): Resource[IO, H2Database] = {
    Resource
      .make(
        IO.delay(
          DriverManager.getConnection(
            h2Settings.getUrl,
            h2Settings.options.getOrElse("user", ""),
            h2Settings.options.getOrElse("password", "")
          )
        ).map(new H2Database(h2Settings, _))
      )(res => {

        val cleanFiles: IO[Unit] = res.h2Settings match {
          case settings: H2InFileSettings =>
            IO.println(
              s"Cleaning H2 folder for DB '${settings.name}' in '${settings.dir}'..."
            )

            //          .filter(file => file.getAbsolutePath.contains(settings.name) && file.getAbsolutePath.endsWith("db"))
            new JFile(settings.parentDir)
              .listFiles()
              .toList
              .traverse(file => {
                IO.println(s"Deleting ${file.getAbsolutePath}") >> IO(file.delete())
              })
              .void

          case _ => IO.unit
        }

        (
          cleanFiles,
          IO(res.h2Connection.close())
        ).parTupled.void
      })
  }
}
