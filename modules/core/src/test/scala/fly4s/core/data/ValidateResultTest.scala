package fly4s.core.data

import cats.data.{NonEmptyList, ValidatedNel}
import cats.data.Validated.{Invalid, Valid}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.{Success, Try}

class ValidateResultTest extends AnyFunSuite with Matchers {

  test("ValidateResult success toValidatedNel should be Success(Valid)") {

    val validateResult = Samples.anValidateResult(
      validationSuccessful = true,
      invalidMigrations    = Nil
    )
    val result: Try[ValidatedNel[ValidateOutput, Unit]] = ValidateResult.toValidatedNel[Try](validateResult)

    result shouldBe Success(Valid(()))
  }

  test("ValidateResult failed with errors toValidatedNel should be Success(Invalid)") {

    val invalidMigrations = NonEmptyList.one(Samples.aValidateOutput)
    val validateResult = Samples.anValidateResult(
      validationSuccessful = false,
      invalidMigrations    = invalidMigrations.toList
    )

    val result = ValidateResult.toValidatedNel[Try](validateResult)

    result shouldBe Success(Invalid(invalidMigrations))
  }

  test("ValidateResult failed without errors toValidatedNel should be Failure") {

    val validateResult = Samples.anValidateResult(
      validationSuccessful = false,
      invalidMigrations    = Nil
    )

    val result = ValidateResult.toValidatedNel[Try](validateResult)

    result.isFailure shouldBe true
  }
}
