package fly4s.core

import cats.effect.IO
import fly4s.core.data.{Fly4sConfig, Location}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import utils.{H2Settings, H2TestSupport}

class Fly4sTest extends AnyFunSuite with H2TestSupport with Matchers {

  val h2Settings: H2Settings = H2Settings.inMemory(
    name = "h2-test",
    options = Map(
      "MODE"           -> "MYSQL",
      "DB_CLOSE_DELAY" -> "-1"
    )
  )

  test("Test migration") {

    val result =
      Fly4s(
        Fly4sConfig(
          url = h2Settings.getUrl(),
          locations = Location.of("/migrations")
        )
      ).migrate[IO].unsafeRunSync()

    result.migrationsExecuted shouldBe 2
  }
}
