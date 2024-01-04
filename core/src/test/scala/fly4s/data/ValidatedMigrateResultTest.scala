package fly4s.data

import cats.data.NonEmptyList

import scala.util.{Success, Try}

class ValidatedMigrateResultTest extends munit.FunSuite {

  import ValidateOutput.*

  test("ValidatedMigrateResult.valid.liftTo[Try] should be Success") {
    val migrateResult = new MigrateResult("FLYWAY_VERSION", "DATABASE", "SCHEMA_NAME")
    val result = ValidatedMigrateResult.liftTo[Try](ValidatedMigrateResult.valid(migrateResult))

    assertEquals(
      obtained = result,
      expected = Success(migrateResult)
    )
  }

  test("ValidatedMigrateResult.invalid.liftTo[Try] should be Failure") {
    val validateOutputs = NonEmptyList.of(Samples.aValidateOutput)
    val result = ValidatedMigrateResult.liftTo[Try](ValidatedMigrateResult.invalid(validateOutputs))

    assertEquals(
      obtained = result.isFailure,
      expected = true
    )
  }
}
