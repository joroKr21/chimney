package io.scalaland.chimney.fixtures
package numbers

// following https://en.wikipedia.org/wiki/Names_of_large_numbers

package short {
  sealed trait NumScale[+A, Dummy]
  case object Zero extends NumScale[Nothing, Nothing]
  case class Million[A](count: A) extends NumScale[A, Nothing] // 10^6
  case class Billion[A](count: A) extends NumScale[A, Nothing] // 10^9
  case class Trillion[A](count: A) extends NumScale[A, Nothing] // 10^12
}

package long {
  sealed trait NumScale[+A]
  case object Zero extends NumScale[Nothing]
  case class Million[A](count: A) extends NumScale[A] // 10^6
  case class Milliard[A](count: A) extends NumScale[A] // 10^9
  case class Billion[A](count: A) extends NumScale[A] // 10^12
  case class Billiard[A](count: A) extends NumScale[A] // 10^15
  case class Trillion[A](count: A) extends NumScale[A] // 10^18
}

import io.scalaland.chimney.{PartialTransformer, Transformer}

object ScalesPartialTransformer {

  import io.scalaland.chimney.dsl.*

  implicit def shortToLongTotalInner[A, B](implicit
      ft: Transformer[A, B]
  ): PartialTransformer[short.NumScale[A, Nothing], long.NumScale[B]] =
    Transformer
      .definePartial[short.NumScale[A, Nothing], long.NumScale[B]]
      .withSealedSubtypeHandledPartial { (billion: short.Billion[A]) =>
        billion.transformIntoPartial[long.Milliard[B]]
      }
      .withSealedSubtypeHandledPartial { (trillion: short.Trillion[A]) =>
        trillion.transformIntoPartial[long.Billion[B]]
      }
      .buildTransformer

  implicit def shortToLongPartialInner[A, B](implicit
      ft: PartialTransformer[A, B]
  ): PartialTransformer[short.NumScale[A, Nothing], long.NumScale[B]] =
    Transformer
      .definePartial[short.NumScale[A, Nothing], long.NumScale[B]]
      .withSealedSubtypeHandledPartial { (billion: short.Billion[A]) =>
        billion.transformIntoPartial[long.Milliard[B]]
      }
      .withSealedSubtypeHandledPartial { (trillion: short.Trillion[A]) =>
        trillion.transformIntoPartial[long.Billion[B]]
      }
      .buildTransformer
}
