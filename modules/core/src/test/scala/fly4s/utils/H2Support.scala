package fly4s.utils

import cats.effect.{IO, Resource}
import org.scalatest.{BeforeAndAfterAll, Suite}

import java.io.File as JFile
import java.sql.{Connection, DriverManager}

trait H2Support extends BeforeAndAfterAll { this: Suite =>

  val h2Settings: H2Settings

  final val h2Connection: Resource[IO, Connection] =
    Resource.fromAutoCloseable(
      IO.delay(
        DriverManager.getConnection(
          h2Settings.getUrl,
          h2Settings.options.getOrElse("user", ""),
          h2Settings.options.getOrElse("password", "")
        )
      )
    )

  def executeUpdate(sql: String): IO[Int] =
    h2Connection.use(c =>
      IO {
        c.createStatement.executeUpdate(sql)
      }
    )

  def dropAllTables: IO[Int] =
    executeUpdate("DROP ALL OBJECT")

  override def afterAll(): Unit =
    h2Settings match {
      case settings: H2InFileSettings =>
        Console.out.println(s"Cleaning H2 folder for DB '${settings.name}' in '${settings.dir}'...")

        new JFile(settings.parentDir)
          .listFiles()
//          .filter(file => file.getAbsolutePath.contains(settings.name) && file.getAbsolutePath.endsWith("db"))
          .foreach(file => {
            Console.out.println(s"Deleting ${file.getAbsolutePath}")
            file.delete()
          })

      case _ => ()
    }
}
