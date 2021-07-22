//package fly4s.core
//
//import cats.effect.IO
//import org.scalatest.funsuite.AnyFunSuite
//import utils.{H2Settings, H2TestSupport}
//
//class Fly4sTest extends AnyFunSuite with H2TestSupport {
//
//  val h2Settings: H2Settings = H2Settings.inMemory("h2-mem-test")
//
//  import Fly4s._
//
//  test("Test migration") {
//
//    val result = Fly4s
//      .migrate[IO](
//        url = h2Settings.url,
//        migrationsLocations = List("")
//      )
//      .value
//
//    Console.println(result.unsafeRunSync())
//  }
//}
