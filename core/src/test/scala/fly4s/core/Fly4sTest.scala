package fly4s.core

import cats.effect.{IO, SyncIO}
import fly4s.utils.{H2Database, H2Settings}

class Fly4sTest extends munit.CatsEffectSuite {

  import cats.implicits.*
  import fly4s.core.data.*
  import fly4s.implicits.*

  val fixture: SyncIO[FunFixture[H2Database]] = ResourceFixture(
    H2Database.make(
      H2Settings.inMemory(
        name = "h2-test",
        options = Map(
          "MODE"     -> "MYSQL",
          "user"     -> "USER",
          "password" -> "PWD"
          //      "DB_CLOSE_DELAY" -> "-1"
        )
      )
    )
  )

  fixture.test("Test validate and migrate") { fixture =>
    assertIO(
      obtained = Fly4s
        .make[IO](
          url = fixture.h2Settings.getUrl,
          config = Fly4sConfig(
            locations = List(Location("/migrations")),
            ignoreMigrationPatterns = List(
              ValidatePattern.ignorePendingMigrations
            )
          )
        )
        .use(_.validateAndMigrate.result)
        .map(_.migrationsExecuted),
      returns = 2
    )
  }

  fixture.test("Test migrate") { fixture =>
    assertIO(
      obtained = Fly4s
        .make[IO](
          url = fixture.h2Settings.getUrl,
          config = Fly4sConfig(
            locations = List(Location("/migrations"))
          )
        )
        .use(_.migrate)
        .map(_.migrationsExecuted),
      returns = 2
    )
  }

  fixture.test("Test validate") { fixture =>
    assertIO_(
      Fly4s
        .make[IO](
          url = fixture.h2Settings.getUrl,
          config = Fly4sConfig(
            locations = List(Location("/migrations"))
          )
        )
        .use(_.validate)
        .void
    )
  }

  fixture.test("Test clean") { fixture =>
    assertIO_(
      Fly4s
        .make[IO](
          url = fixture.h2Settings.getUrl,
          config = Fly4sConfig(
            locations     = List(Location("/migrations")),
            cleanDisabled = false
          )
        )
        .use(_.clean)
        .void
    )
  }

  fixture.test("Test baseline") { fixture =>
    assertIO(
      obtained = Fly4s
        .make[IO](
          url = fixture.h2Settings.getUrl,
          config = Fly4sConfig(
            locations = List(Location("/migrations"))
          )
        )
        .use(_.baseline)
        .map(_.successfullyBaselined),
      returns = true
    )
  }

  fixture.test("Test repair") { fixture =>
    assertIO(
      obtained = Fly4s
        .make[IO](
          url = fixture.h2Settings.getUrl,
          config = Fly4sConfig(
            locations = List(Location("/migrations"))
          )
        )
        .use(_.repair)
        .map(_.repairActions.size()),
      returns = 0
    )
  }

  fixture.test("Test info") { fixture =>
    assertIO_(
      Fly4s
        .make[IO](
          url = fixture.h2Settings.getUrl,
          config = Fly4sConfig(
            locations = List(Location("/migrations"))
          )
        )
        .use(_.info)
        .void
    )
  }

  fixture.test("Reconfigure with a completely new config") { fixture =>
    val res: IO[Fly4s[IO]] = Fly4s
      .make[IO](
        url      = fixture.h2Settings.getUrl,
        user     = "USER".some,
        password = "PWD".toCharArray.some,
        config = Fly4sConfig(
          locations = List(Location("/migrations"))
        )
      )
      .use(
        _.reconfigure(
          Fly4sConfig(
            locations = List(Location("/new_migrations"))
          )
        )
      )

    for {
      _ <- assertIO(
        obtained = res.map(_.config.locations),
        returns  = List(Location("/new_migrations"))
      )
      _ <- assertIO(
        obtained = res.map(_.sourceConfig.url),
        returns  = Some(fixture.h2Settings.getUrl)
      )
      _ <- assertIO(
        obtained = res.map(_.sourceConfig.user),
        returns  = Some("USER")
      )
      _ <- assertIO(
        obtained = res.map(_.sourceConfig.password.map(_.mkString)),
        returns  = Some("PWD")
      )
    } yield ()
  }

  fixture.test("Reconfigure mapping current config") { fixture =>
    val res: IO[Fly4s[IO]] = Fly4s
      .make[IO](
        url      = fixture.h2Settings.getUrl,
        user     = "USER".some,
        password = "PWD".toCharArray.some,
        config = Fly4sConfig(
          locations = List(Location("/migrations"))
        )
      )
      .use(
        _.reconfigure((current: Fly4sConfig) =>
          current.copy(
            locations = List(Location("/new_migrations"))
          )
        )
      )

    for {
      _ <- assertIO(
        obtained = res.map(_.config.locations),
        returns  = List(Location("/new_migrations"))
      )
      _ <- assertIO(
        obtained = res.map(_.sourceConfig.url),
        returns  = Some(fixture.h2Settings.getUrl)
      )
      _ <- assertIO(
        obtained = res.map(_.sourceConfig.user),
        returns  = Some("USER")
      )
      _ <- assertIO(
        obtained = res.map(_.sourceConfig.password.map(_.mkString)),
        returns  = Some("PWD")
      )
    } yield ()
  }
}
