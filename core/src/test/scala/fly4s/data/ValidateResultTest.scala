package fly4s.data

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}

import scala.util.{Success, Try}

class ValidateResultTest extends munit.FunSuite {

  test("ValidateResult success toValidatedNel should be Success(Valid)") {

    val validateResult = Samples.anValidateResult(
      validationSuccessful = true,
      invalidMigrations    = Nil
    )

    assertEquals(
      obtained = ValidateResult.toValidatedNel[Try](validateResult),
      expected = Success(Valid(()))
    )
  }

  test("ValidateResult failed with errors toValidatedNel should be Success(Invalid)") {

    val invalidMigrations = NonEmptyList.one(Samples.aValidateOutput)
    val validateResult = Samples.anValidateResult(
      validationSuccessful = false,
      invalidMigrations    = invalidMigrations.toList
    )

    assertEquals(
      obtained = ValidateResult.toValidatedNel[Try](validateResult),
      expected = Success(Invalid(invalidMigrations))
    )
  }

  test("ValidateResult failed without errors toValidatedNel should be Failure") {

    val validateResult = Samples.anValidateResult(
      validationSuccessful = false,
      invalidMigrations    = Nil
    )

    assertEquals(
      obtained = ValidateResult.toValidatedNel[Try](validateResult).isFailure,
      expected = true
    )
  }
}
