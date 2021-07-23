package fly4s.core

import cats.effect.IO
import org.scalatest.funsuite.AnyFunSuite
import utils.{H2Settings, H2TestSupport}

class Fly4sTest extends AnyFunSuite with H2TestSupport {

  val h2Settings: H2Settings = H2Settings.inFile(
    name = "h2-test",
    options = Map(
      "MODE"           -> "MYSQL",
      "DB_CLOSE_DELAY" -> "-1"
    )
  )

  import Fly4s._

  test("Test migration") {

    val result = Fly4s
      .migrate[IO](
        url = h2Settings.getUrl(),
        migrationsLocations = List("/migrations")
      )
      .value

    Console.println(result.unsafeRunSync())

    Thread.sleep(100000)
  }
}
