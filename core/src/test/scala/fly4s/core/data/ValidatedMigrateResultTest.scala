package fly4s.core.data

import cats.data.NonEmptyList
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.TryValues

import scala.util.{Success, Try}

class ValidatedMigrateResultTest extends AnyFunSuite with Matchers with TryValues {

  import ValidateOutput.*

  test("ValidatedMigrateResult.valid.liftTo[Try] should be Success") {
    val migrateResult = new MigrateResult("FLYWAY_VERSION", "DATABASE", "SCHEMA_NAME")
    val result = ValidatedMigrateResult.liftTo[Try](ValidatedMigrateResult.valid(migrateResult))

    result shouldBe Success(migrateResult)
  }

  test("ValidatedMigrateResult.invalid.liftTo[Try] should be Failure") {
    val validateOutputs = NonEmptyList.of(Samples.aValidateOutput)
    val result = ValidatedMigrateResult.liftTo[Try](ValidatedMigrateResult.invalid(validateOutputs))

    result.isFailure shouldBe true
  }
}
