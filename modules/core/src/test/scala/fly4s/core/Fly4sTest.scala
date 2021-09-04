package fly4s.core

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import fly4s.core.data.*
import fly4s.utils.{H2Settings, H2Support}
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers

class Fly4sTest extends AsyncFunSuite with AsyncIOSpec with Matchers with H2Support {

  import cats.implicits.*
  import fly4s.implicits.*

  val h2Settings: H2Settings = H2Settings.inMemory(
    name = "h2-test",
    options = Map(
      "MODE" -> "MYSQL"
//      "DB_CLOSE_DELAY" -> "-1"
    )
  )

  test("Test validate and migrate") {
    Fly4s
      .make[IO](
        url = h2Settings.getUrl,
        config = Fly4sConfig(
          locations               = Location.ofAll("/migrations"),
          ignorePendingMigrations = true
        )
      )
      .use(_.validateAndMigrate[IO])
      .result
      .asserting(_.migrationsExecuted shouldBe 2)
  }

  test("Test migrate") {
    Fly4s
      .make[IO](
        url = h2Settings.getUrl,
        config = Fly4sConfig(
          locations = Location.ofAll("/migrations")
        )
      )
      .use(_.migrate[IO])
      .asserting(_.migrationsExecuted shouldBe 2)
  }

  test("Test validate") {
    Fly4s
      .make[IO](
        url = h2Settings.getUrl,
        config = Fly4sConfig(
          locations = Location.ofAll("/migrations")
        )
      )
      .use(_.validate[IO])
      .assertNoException
  }

  test("Test clean") {
    Fly4s
      .make[IO](
        url = h2Settings.getUrl,
        config = Fly4sConfig(
          locations = Location.ofAll("/migrations")
        )
      )
      .use(_.clean[IO])
      .assertNoException
  }

  test("Test baseline") {
    Fly4s
      .make[IO](
        url = h2Settings.getUrl,
        config = Fly4sConfig(
          locations = Location.ofAll("/migrations")
        )
      )
      .use(_.baseline[IO])
      .asserting(_.successfullyBaselined shouldBe true)
  }

  test("Test repair") {
    Fly4s
      .make[IO](
        url = h2Settings.getUrl,
        config = Fly4sConfig(
          locations = Location.ofAll("/migrations")
        )
      )
      .use(_.repair[IO])
      .asserting(_.repairActions shouldBe empty)
  }

  test("Test info") {
    Fly4s
      .make[IO](
        url = h2Settings.getUrl,
        config = Fly4sConfig(
          locations = Location.ofAll("/migrations")
        )
      )
      .use(_.info[IO])
      .assertNoException
  }

  test("Reconfigure with a completely new config") {

    val res = Fly4s
      .make[IO](
        url      = h2Settings.getUrl,
        user     = "USER".some,
        password = "PWD".toCharArray.some,
        config = Fly4sConfig(
          locations = Location.ofAll("/migrations")
        )
      )
      .use(
        _.reconfigure[IO](
          Fly4sConfig(
            locations = Location.ofAll("/new_migrations")
          )
        )
      )

    for {
      _ <- res.asserting(_.config.locations shouldBe List(Location("/new_migrations")))
      _ <- res.asserting(_.sourceConfig.url shouldBe Some(h2Settings.getUrl))
      _ <- res.asserting(_.sourceConfig.user shouldBe Some("USER"))
      _ <- res.asserting(_.sourceConfig.password.map(_.mkString) shouldBe Some("PWD"))
    } yield ()
  }

  test("Reconfigure mapping current config") {

    val res = Fly4s
      .make[IO](
        url      = h2Settings.getUrl,
        user     = "USER".some,
        password = "PWD".toCharArray.some,
        config = Fly4sConfig(
          locations = Location.ofAll("/migrations")
        )
      )
      .use(
        _.reconfigure[IO]((current: Fly4sConfig) =>
          current.copy(
            locations = Location.ofAll("/new_migrations")
          )
        )
      )

    for {
      _ <- res.asserting(_.config.locations shouldBe List(Location("/new_migrations")))
      _ <- res.asserting(_.sourceConfig.url shouldBe Some(h2Settings.getUrl))
      _ <- res.asserting(_.sourceConfig.user shouldBe Some("USER"))
      _ <- res.asserting(_.sourceConfig.password.map(_.mkString) shouldBe Some("PWD"))
    } yield ()
  }
}
