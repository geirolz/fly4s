package fly4s.core

import cats.effect.IO
import fly4s.core.data.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import fly4s.utils.{H2Settings, H2TestSupport}

class Fly4sTest extends AnyFunSuite with Matchers with H2TestSupport {

  import cats.effect.unsafe.implicits.global
  import fly4s.implicits.*

  val h2Settings: H2Settings = H2Settings.inMemory(
    name = "h2-test",
    options = Map(
      "MODE" -> "MYSQL"
//      "DB_CLOSE_DELAY" -> "-1"
    )
  )

  test("Test validate and migrate") {
    val result: MigrateResult =
      Fly4s(
        Fly4sConfig(
          url = h2Settings.getUrl,
          locations = List(Location("/migrations"))
        )
      )
        .validateAndMigrate[IO]
        .result
        .unsafeRunSync()

    result.migrationsExecuted shouldBe 2
  }

  test("Test migrate") {

    val result: MigrateResult = Fly4s(
      Fly4sConfig(
        url = h2Settings.getUrl,
        locations = List(Location("/migrations"))
      )
    ).migrate[IO].unsafeRunSync()

    result.migrationsExecuted shouldBe 2
  }

  test("Test validate") {
    Fly4s(
      Fly4sConfig(
        url = h2Settings.getUrl,
        locations = Location.ofAll("/migrations")
      )
    ).validate[IO].unsafeRunSync()
  }

  test("Test clean") {
    Fly4s(
      Fly4sConfig(
        url = h2Settings.getUrl,
        locations = Location.ofAll("/migrations")
      )
    ).clean[IO].unsafeRunSync()
  }

  test("Test baseline") {
    val result: BaselineResult =
      Fly4s(
        Fly4sConfig(
          url = h2Settings.getUrl,
          locations = Location.ofAll("/migrations")
        )
      ).baseline[IO].unsafeRunSync()

    result.successfullyBaselined shouldBe true
  }

  test("Test repair") {
    val result: RepairResult =
      Fly4s(
        Fly4sConfig(
          url = h2Settings.getUrl,
          locations = Location.ofAll("/migrations")
        )
      ).repair[IO].unsafeRunSync()

    result.repairActions shouldBe empty
  }

  test("Test info") {
    Fly4s(
      Fly4sConfig(
        url = h2Settings.getUrl,
        locations = Location.ofAll("/migrations")
      )
    ).info[IO].unsafeRunSync()
  }
}
