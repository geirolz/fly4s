package fly4s.utils

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfterAll
import java.io.{File => JFile}

trait H2TestSupport extends BeforeAndAfterAll { this: AnyFunSuite =>

  val h2Settings: H2Settings

  override def afterAll(): Unit =
    h2Settings match {
      case settings: H2InFileSettings =>
        Console.out.println(s"Cleaning H2 folder for DB '${settings.name}' in '${settings.dir}'...")

        new JFile(settings.parentDir)
          .listFiles()
          .filter(file => file.getAbsolutePath.contains(settings.name) && file.getAbsolutePath.endsWith("db"))
          .foreach(file => {
            Console.out.println(s"Deleting ${file.getAbsolutePath}")
            file.delete()
          })

      case _ => ()
    }
}
