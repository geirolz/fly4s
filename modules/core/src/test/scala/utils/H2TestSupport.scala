package utils

import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

trait H2TestSupport extends BeforeAndAfterAll { this: AnyFunSuite =>

  val h2Settings: H2Settings

  import reflect.io._
  import Path._

  override def afterAll(): Unit =
    h2Settings match {
      case settings: H2InFileSettings =>
        Console.out.println(s"Cleaning H2 folder for DB '${settings.name}' in '${settings.dir}'...")

        settings.parentDir.toDirectory.files
          .filter(file => file.path.contains(settings.name) && file.extension.equals("db"))
          .foreach(file => {
            Console.out.println(s"Deleting ${file.toAbsolute.path}")
            file.deleteRecursively()
          })

      case _ => ()
    }
}
