package fly4s.core

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import fly4s.utils.{H2Settings, H2Support}
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers

class Fly4sTest extends AsyncFunSuite with AsyncIOSpec with Matchers with H2Support {

  import cats.implicits.*
  import fly4s.implicits.*
  import fly4s.core.data.*

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
          locations = Location.of("/migrations"),
          ignoreMigrationPatterns = List(
            ValidatePattern.ignorePendingMigrations
          )
        )
      )
      .use(_.validateAndMigrate.result)
      .asserting(_.migrationsExecuted shouldBe 2)
  }

  test("Test migrate") {
    Fly4s
      .make[IO](
        url = h2Settings.getUrl,
        config = Fly4sConfig(
          locations = Location.of("/migrations")
        )
      )
      .use(_.migrate)
      .asserting(_.migrationsExecuted shouldBe 2)
  }

  test("Test validate") {
    Fly4s
      .make[IO](
        url = h2Settings.getUrl,
        config = Fly4sConfig(
          locations = Location.of("/migrations")
        )
      )
      .use(_.validate)
      .assertNoException
  }

  test("Test clean") {
    Fly4s
      .make[IO](
        url = h2Settings.getUrl,
        config = Fly4sConfig(
          locations     = Location.of("/migrations"),
          cleanDisabled = false
        )
      )
      .use(_.clean)
      .assertNoException
  }

  test("Test baseline") {
    Fly4s
      .make[IO](
        url = h2Settings.getUrl,
        config = Fly4sConfig(
          locations = Location.of("/migrations")
        )
      )
      .use(_.baseline)
      .asserting(_.successfullyBaselined shouldBe true)
  }

  test("Test repair") {
    Fly4s
      .make[IO](
        url = h2Settings.getUrl,
        config = Fly4sConfig(
          locations = Location.of("/migrations")
        )
      )
      .use(_.repair)
      .asserting(_.repairActions shouldBe empty)
  }

  test("Test info") {
    Fly4s
      .make[IO](
        url = h2Settings.getUrl,
        config = Fly4sConfig(
          locations = Location.of("/migrations")
        )
      )
      .use(_.info)
      .assertNoException
  }

  test("Reconfigure with a completely new config") {

    val res: IO[Fly4s[IO]] = Fly4s
      .make[IO](
        url      = h2Settings.getUrl,
        user     = "USER".some,
        password = "PWD".toCharArray.some,
        config = Fly4sConfig(
          locations = Location.of("/migrations")
        )
      )
      .use(
        _.reconfigure(
          Fly4sConfig(
            locations = Location.of("/new_migrations")
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

    val res: IO[Fly4s[IO]] = Fly4s
      .make[IO](
        url      = h2Settings.getUrl,
        user     = "USER".some,
        password = "PWD".toCharArray.some,
        config = Fly4sConfig(
          locations = Location.of("/migrations")
        )
      )
      .use(
        _.reconfigure((current: Fly4sConfig) =>
          current.copy(
            locations = Location.of("/new_migrations")
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
