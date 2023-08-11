package fly4s.core

import cats.effect.IO
import fly4s.core.data.*
import fly4s.utils.H2Settings
import org.flywaydb.core.Flyway

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.{DurationInt, FiniteDuration}

class Fly4sPerformanceSuite extends munit.CatsEffectSuite {

  import cats.implicits.*

  test("Check performance for 'migrate'") {

    val op       = "migrate"
    val fly4sDb  = H2Settings.inMemory(s"fl4s$op").mysqlMode
    val flywayDb = H2Settings.inMemory(s"flyway$op").mysqlMode

    checkPerformance(op)(
      tolerance = 1000.milliseconds,
      fly4sOp = Fly4s
        .make[IO](
          url = fly4sDb.getUrl,
          config = Fly4sConfig(
            locations = Locations("/migrations")
          )
        )
        .use(_.migrate),
      flywayOp = IO {
        Flyway
          .configure()
          .dataSource(flywayDb.getUrl, null, null)
          .locations("/migrations")
          .load()
          .migrate()
      }
    )
  }

  test("Check performance for 'validate'") {

    val op       = "validate"
    val fly4sDb  = H2Settings.inMemory(s"fl4s$op").mysqlMode
    val flywayDb = H2Settings.inMemory(s"flyway$op").mysqlMode

    checkPerformance(op)(
      tolerance = 500.milliseconds,
      fly4sOp = Fly4s
        .make[IO](
          url = fly4sDb.getUrl,
          config = Fly4sConfig(
            locations = Locations("/migrations"),
            ignoreMigrationPatterns = List(
              ValidatePattern.ignorePendingMigrations
            )
          )
        )
        .use(_.validate),
      flywayOp = IO {
        Flyway
          .configure()
          .dataSource(flywayDb.getUrl, null, null)
          .locations("/migrations")
          .ignoreMigrationPatterns(
            ValidatePattern.toPattern(ValidatePattern.ignorePendingMigrations).get
          )
          .load()
          .validate()
      }
    )
  }

  test("Check performance for 'clean'") {

    val op       = "clean"
    val fly4sDb  = H2Settings.inMemory(s"fl4s$op").mysqlMode
    val flywayDb = H2Settings.inMemory(s"flyway$op").mysqlMode

    checkPerformance(op)(
      tolerance = 500.milliseconds,
      fly4sOp = Fly4s
        .make[IO](
          url = fly4sDb.getUrl,
          config = Fly4sConfig(
            locations     = Locations("/migrations"),
            cleanDisabled = false
          )
        )
        .use(_.clean),
      flywayOp = IO {
        Flyway
          .configure()
          .dataSource(flywayDb.getUrl, null, null)
          .locations("/migrations")
          .cleanDisabled(false)
          .load()
          .clean()
      }
    )
  }

  test("Check performance for 'baseline'") {

    val op       = "baseline"
    val fly4sDb  = H2Settings.inMemory(s"fl4s$op").mysqlMode
    val flywayDb = H2Settings.inMemory(s"flyway$op").mysqlMode

    checkPerformance(op)(
      tolerance = 500.milliseconds,
      fly4sOp = Fly4s
        .make[IO](
          url = fly4sDb.getUrl,
          config = Fly4sConfig(
            locations = Locations("/migrations")
          )
        )
        .use(_.baseline),
      flywayOp = IO {
        Flyway
          .configure()
          .dataSource(flywayDb.getUrl, null, null)
          .locations("/migrations")
          .load()
          .baseline()
      }
    )
  }

  test("Check performance for 'repair'") {

    val op       = "repair"
    val fly4sDb  = H2Settings.inMemory(s"fl4s$op").mysqlMode
    val flywayDb = H2Settings.inMemory(s"flyway$op").mysqlMode

    checkPerformance(op)(
      tolerance = 500.milliseconds,
      fly4sOp = Fly4s
        .make[IO](
          url = fly4sDb.getUrl,
          config = Fly4sConfig(
            locations = Locations("/migrations")
          )
        )
        .use(_.repair),
      flywayOp = IO {
        Flyway
          .configure()
          .dataSource(flywayDb.getUrl, null, null)
          .locations("/migrations")
          .load()
          .repair()
      }
    )
  }

  test("Check performance for 'info'") {

    val op       = "info"
    val fly4sDb  = H2Settings.inMemory(s"fl4s$op").mysqlMode
    val flywayDb = H2Settings.inMemory(s"flyway$op").mysqlMode

    checkPerformance(op)(
      tolerance = 500.milliseconds,
      fly4sOp = Fly4s
        .make[IO](
          url = fly4sDb.getUrl,
          config = Fly4sConfig(
            locations = Locations("/migrations")
          )
        )
        .use(_.info),
      flywayOp = IO {
        Flyway
          .configure()
          .dataSource(flywayDb.getUrl, null, null)
          .locations("/migrations")
          .load()
          .info()
      }
    )
  }

  private def checkPerformance(op: String)(
    tolerance: FiniteDuration,
    timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    fly4sOp: IO[?],
    flywayOp: IO[?]
  ): IO[Unit] = {

    val difference = for {
      _            <- IO.println(s"Checking $op...")
      fl4sResult   <- fly4sOp.timed.map(_._1)
      flywayResult <- flywayOp.timed.map(_._1)
      _            <- IO.println(buildReport(op, fl4sResult, flywayResult, timeUnit))
    } yield fl4sResult.toUnit(timeUnit) - flywayResult.toUnit(timeUnit)

    difference.map(diff =>
      assertEqualsDouble(
        obtained = diff,
        expected = diff,
        delta    = tolerance.toUnit(timeUnit)
      )
    )
  }

  private def buildReport(
    op: String,
    fly4sTime: FiniteDuration,
    flywayTime: FiniteDuration,
    timeUnit: TimeUnit
  ): String = {

    val diff = fly4sTime.minus(flywayTime).toUnit(timeUnit)
    val diffMsg =
      if (diff < 0) {
        s"Fly4s is ${diff * -1} $timeUnit faster then Flyway"
      } else {
        s"Fly4s is $diff $timeUnit slower then Flyway"
      }

    s"""
       |OP: $op
       |Fly4s Time: ${fly4sTime.toUnit(timeUnit).show} $timeUnit
       |Flyway Time: ${flywayTime.toUnit(timeUnit).show} $timeUnit
       |
       |$diffMsg
       |""".stripMargin
  }
}
