package fly4s.core

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import fly4s.core.data.*
import fly4s.utils.H2Settings
import org.flywaydb.core.Flyway
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.Assertion

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.{DurationInt, FiniteDuration}

class Fly4sPerformanceTest extends AsyncFunSuite with AsyncIOSpec with Matchers {

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
            locations = Location.of("/migrations")
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
            locations = Location.of("/migrations"),
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
            locations     = Location.of("/migrations"),
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
            locations = Location.of("/migrations")
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
            locations = Location.of("/migrations")
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
            locations = Location.of("/migrations")
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
  ): IO[Assertion] = {

    val result = for {
      _            <- IO(Console.println(s"Checking $op..."))
      fl4sResult   <- fly4sOp.timed
      flywayResult <- flywayOp.timed
    } yield (fl4sResult._1, flywayResult._1)

    result.asserting { case (fly4sTime, flywayTime) =>
      Console.out.println(buildReport(op, fly4sTime, flywayTime, timeUnit))
      fly4sTime.toUnit(timeUnit) shouldBe flywayTime.toUnit(timeUnit) +- tolerance.toUnit(timeUnit)
    }
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
