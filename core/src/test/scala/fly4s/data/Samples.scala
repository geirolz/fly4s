package fly4s.data

import org.flywaydb.core.api.CoreErrorCode

import java.util
import java.util.Collections
import scala.jdk.CollectionConverters.IterableHasAsJava

object Samples {

  def anValidateResult(
    validationSuccessful: Boolean,
    invalidMigrations: List[ValidateOutput]
  ): ValidateResult =
    new ValidateResult(
      /*flywayVersion         = */ "FLYWAY_VERSION",
      /*database              = */ "DATABASE",
      /*errorDetails          = */ Samples.anErrorDetails,
      /*validationSuccessful  = */ validationSuccessful,
      /*validateCount         = */ 0,
      /*invalidMigrations     = */ new util.ArrayList(invalidMigrations.asJavaCollection),
      /*warnings              = */ Collections.emptyList()
    )

  def anErrorDetails: ErrorDetails =
    new ErrorDetails(
      CoreErrorCode.VALIDATE_ERROR,
      "ERROR_MESSAGE"
    )

  def aValidateOutput =
    new ValidateOutput(
      "VERSION",
      "DESCRIPTION",
      "FILE_PATH",
      new ErrorDetails(
        CoreErrorCode.VALIDATE_ERROR,
        "ERROR_MESSAGE"
      )
    )
}
