package com.geirolz.macros.fluentcopy

import com.geirolz.macros.fluentcopy.FluentCopyMacros.FluentCopy
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class FluentCopySuite extends AnyFunSuite with Matchers {

  @FluentCopy
  case class Foo(
    a: Int,
    b: Option[Int],
    c: List[Int]
  )

  test("Foo with fluent copy compiles") {
    assertCompiles(
      """
        |Foo(1, Some(1), List(1))
        |  .withA(10)
        |  .withB(20)
        |  .withB(Some(30))
        |  .withC(50)
        |  .withC(List(60))""".stripMargin
    )
  }

  test("Foo with fluent copy - works as expected") {
    val newFoo = Foo(1, Some(1), List(1))
      .withA(10)
      .withB(20)
      .withB(Some(30))
      .withC(50)
      .withC(List(60))

    newFoo shouldBe Foo(10, Some(30), List(60))
  }
}
