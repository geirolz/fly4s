package utils

import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

import java.io.File
import scala.reflect.io.Directory

sealed trait H2Settings {
  val name: String
  val options: Map[String, String]
  val url: String

  protected def appendOptionsToUrl(url: String): String = {

    val optionsAsString: Option[String] = {
      Option.when(options.nonEmpty) {
        options.map { case (k, v) => s"$k=$v" }.mkString(";")
      }
    }

    optionsAsString match {
      case Some(optionsAsString) => s"$url;$optionsAsString"
      case None                  => url
    }
  }
}
object H2Settings {

  def inMemory(name: String, options: Map[String, String] = Map.empty): H2InMemorySettings =
    H2InMemorySettings(
      name = name,
      options = options
    )

  def inFile(
    name: String,
    dir: String = "./core/test/resources/h2",
    options: Map[String, String] = Map.empty
  ): H2InFileSettings =
    H2InFileSettings(
      name = name,
      dir = dir,
      options = options
    )
}

case class H2InMemorySettings(name: String, options: Map[String, String] = Map.empty) extends H2Settings {
  val url: String = appendOptionsToUrl(s"jdbc:h2:mem:$name")
}
case class H2InFileSettings(name: String, dir: String, options: Map[String, String] = Map.empty) extends H2Settings {
  val url: String = appendOptionsToUrl(s"jdbc:h2:$dir/$name")
}

trait H2TestSupport extends BeforeAndAfterAll { this: AnyFunSuite =>

  val h2Settings: H2Settings

  override def afterAll(): Unit =
    h2Settings match {
      case H2InFileSettings(name, dir, _) =>
        Console.out.println(s"Cleaning H2 folder for DB '$name'...")
        new Directory(new File(dir)).deleteRecursively() match {
          case true  => Console.out.println(s"H2 folder successfully removed of DB '$name'.")
          case false => Console.out.println(s"Unable to remove H2 folder of DB '$name'.")
        }
      case _ => ()
    }
}
