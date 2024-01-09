package io.scalaland.chimney.cats

import _root_.cats.syntax.semigroup.*
import io.scalaland.chimney.{partial, ChimneySpec}
import org.scalacheck.Test.check
import org.scalacheck.Prop.forAll

class PartialTransformerResultErrorLaws extends ChimneySpec {

  test("Semigroup[partial.Result.Errors] should aggregate errors from 2 partial.Result.Errors") {
    check(forAll { (str1: String, str2: String) =>
      (partial.Result.Errors.fromString(str1) |+| partial.Result.Errors.fromString(str2)) ==
        partial.Result.Errors.fromStrings(str1, str2)
    })
  }
}