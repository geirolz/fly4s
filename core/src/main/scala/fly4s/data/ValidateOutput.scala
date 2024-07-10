package fly4s.data

import cats.Show

object ValidateOutput extends ValidateOutputInstances {

  def asPrettyString(error: ValidateOutput): String =
    s"""
       |Failed validation:
       |  - version: ${error.version}
       |  - path: ${error.filepath}
       |  - description: ${error.description}
       |  - errorCode: ${error.errorDetails.errorCode}
       |  - errorMessage: ${error.errorDetails.errorMessage}
                """.stripMargin
}

trait ValidateOutputInstances {

  implicit val showInstanceForValidateOutput: Show[ValidateOutput] = (v: ValidateOutput) =>
    ValidateOutput.asPrettyString(v)

  implicit def showInstanceForValidateOutputList(implicit
    S: Show[ValidateOutput]
  ): Show[Iterable[ValidateOutput]] =
    (vs: Iterable[ValidateOutput]) =>
      vs
        .map(S.show)
        .toList
        .mkString("\n\n")
}
