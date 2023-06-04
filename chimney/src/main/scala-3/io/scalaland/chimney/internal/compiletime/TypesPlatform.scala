package io.scalaland.chimney.internal.compiletime

import io.scalaland.chimney.dsl as dsls
import io.scalaland.chimney.internal
import io.scalaland.chimney.{partial, PartialTransformer, Patcher, Transformer}

import scala.quoted

private[compiletime] trait TypesPlatform extends Types { this: DefinitionsPlatform =>

  import quotes.*
  import quotes.reflect.*

  final override type Type[T] = quoted.Type[T]

  object Type extends TypeModule {

    val Nothing: Type[Nothing] = quoted.Type.of[Nothing]
    val Any: Type[Any] = quoted.Type.of[Any]
    val Boolean: Type[Boolean] = quoted.Type.of[Boolean]
    val Int: Type[Int] = quoted.Type.of[Int]
    val Unit: Type[Unit] = quoted.Type.of[Unit]

    def Tuple2[A: Type, B: Type]: Type[(A, B)] = quoted.Type.of[(A, B)]

    def Function1[A: Type, B: Type]: Type[A => B] = quoted.Type.of[A => B]
    def Function2[A: Type, B: Type, C: Type]: Type[(A, B) => C] = quoted.Type.of[(A, B) => C]

    object Array extends ArrayModule {
      def apply[T: Type]: Type[Array[T]] = quoted.Type.of[Array[T]]
    }

    object Option extends OptionModule {

      def apply[T: Type]: Type[Option[T]] = quoted.Type.of[Option[T]]
      def unapply[T](tpe: Type[T]): Option[ComputedType] = tpe match {
        case '[Option[inner]] => Some(ComputedType(Type[inner]))
        case _                => scala.None
      }

      val None: Type[scala.None.type] = quoted.Type.of[scala.None.type]
    }

    object Either extends EitherModule {
      def apply[L: Type, R: Type]: Type[Either[L, R]] = quoted.Type.of[Either[L, R]]
      def Left[L: Type, R: Type]: Type[Left[L, R]] = quoted.Type.of[Left[L, R]]
      def Right[L: Type, R: Type]: Type[Right[L, R]] = quoted.Type.of[Right[L, R]]
    }

    def isSubtypeOf[S, T](S: Type[S], T: Type[T]): Boolean = TypeRepr.of(using S) <:< TypeRepr.of(using T)
    def isSameAs[S, T](S: Type[S], T: Type[T]): Boolean = TypeRepr.of(using S) =:= TypeRepr.of(using T)

    def prettyPrint[T: Type]: String = {
      val repr = TypeRepr.of[T]
      scala.util.Try(repr.dealias.show(using Printer.TypeReprAnsiCode)).getOrElse(repr.toString)
    }
  }
}
