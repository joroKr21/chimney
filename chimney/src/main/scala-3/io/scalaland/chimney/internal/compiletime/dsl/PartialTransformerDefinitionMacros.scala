package io.scalaland.chimney.internal.compiletime.dsl

import io.scalaland.chimney.PartialTransformer
import io.scalaland.chimney.dsl.*
import io.scalaland.chimney.internal.compiletime.derivation.transformer.TransformerMacros
import io.scalaland.chimney.internal.compiletime.dsl.utils.DslMacroUtils
import io.scalaland.chimney.internal.runtime.{
  ArgumentLists,
  Path,
  TransformerFlags,
  TransformerOverrides,
  WithRuntimeDataStore
}
import io.scalaland.chimney.internal.runtime.TransformerOverrides.*
import io.scalaland.chimney.partial

import scala.quoted.*

object PartialTransformerDefinitionMacros {

  def withFieldConstImpl[
      From: Type,
      To: Type,
      Tail <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type,
      U: Type
  ](
      td: Expr[PartialTransformerDefinition[From, To, Tail, Flags]],
      selector: Expr[To => T],
      value: Expr[U]
  )(using Quotes): Expr[PartialTransformerDefinition[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyFieldNameType {
      [toPath <: Path] =>
        (_: Type[toPath]) ?=>
          '{
            WithRuntimeDataStore
              .update($td, $value)
              .asInstanceOf[PartialTransformerDefinition[From, To, Const[toPath, Tail], Flags]]
        }
    }(selector)

  def withFieldConstPartialImpl[
      From: Type,
      To: Type,
      Tail <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type,
      U: Type
  ](
      td: Expr[PartialTransformerDefinition[From, To, Tail, Flags]],
      selector: Expr[To => T],
      value: Expr[partial.Result[U]]
  )(using Quotes): Expr[PartialTransformerDefinition[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyFieldNameType {
      [toPath <: Path] =>
        (_: Type[toPath]) ?=>
          '{
            WithRuntimeDataStore
              .update($td, $value)
              .asInstanceOf[PartialTransformerDefinition[
                From,
                To,
                ConstPartial[toPath, Tail],
                Flags
              ]]
        }
    }(selector)

  def withFieldComputedImpl[
      From: Type,
      To: Type,
      Tail <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type,
      U: Type
  ](
      td: Expr[PartialTransformerDefinition[From, To, Tail, Flags]],
      selector: Expr[To => T],
      f: Expr[From => U]
  )(using Quotes): Expr[PartialTransformerDefinition[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyFieldNameType {
      [toPath <: Path] =>
        (_: Type[toPath]) ?=>
          '{
            WithRuntimeDataStore
              .update($td, $f)
              .asInstanceOf[PartialTransformerDefinition[From, To, Computed[toPath, Tail], Flags]]
        }
    }(selector)

  def withFieldComputedPartialImpl[
      From: Type,
      To: Type,
      Tail <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type,
      U: Type
  ](
      td: Expr[PartialTransformerDefinition[From, To, Tail, Flags]],
      selector: Expr[To => T],
      f: Expr[From => partial.Result[U]]
  )(using Quotes): Expr[PartialTransformerDefinition[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyFieldNameType {
      [toPath <: Path] =>
        (_: Type[toPath]) ?=>
          '{
            WithRuntimeDataStore
              .update($td, $f)
              .asInstanceOf[PartialTransformerDefinition[
                From,
                To,
                ComputedPartial[toPath, Tail],
                Flags
              ]]
        }
    }(selector)

  def withFieldRenamed[
      From: Type,
      To: Type,
      Tail <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type,
      U: Type
  ](
      td: Expr[PartialTransformerDefinition[From, To, Tail, Flags]],
      selectorFrom: Expr[From => T],
      selectorTo: Expr[To => U]
  )(using Quotes): Expr[PartialTransformerDefinition[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyFieldNameTypes {
      [fromPath <: Path, toPath <: Path] =>
        (_: Type[fromPath]) ?=>
          (_: Type[toPath]) ?=>
            '{
              $td.asInstanceOf[
                PartialTransformerDefinition[From, To, RenamedFrom[fromPath, toPath, Tail], Flags]
              ]
          }
    }(selectorFrom, selectorTo)

  def withCoproductInstance[
      From: Type,
      To: Type,
      Tail <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      Subtype: Type
  ](
      td: Expr[PartialTransformerDefinition[From, To, Tail, Flags]],
      f: Expr[Subtype => To]
  )(using Quotes): Expr[PartialTransformerDefinition[From, To, ? <: TransformerOverrides, Flags]] =
    '{
      WithRuntimeDataStore
        .update($td, $f)
        .asInstanceOf[PartialTransformerDefinition[From, To, CaseComputed[Path.Match[Path.Root, Subtype], Tail], Flags]]
    }

  def withCoproductInstancePartial[
      From: Type,
      To: Type,
      Tail <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      Subtype: Type
  ](
      td: Expr[PartialTransformerDefinition[From, To, Tail, Flags]],
      f: Expr[Subtype => partial.Result[To]]
  )(using Quotes): Expr[PartialTransformerDefinition[From, To, ? <: TransformerOverrides, Flags]] =
    '{
      WithRuntimeDataStore
        .update($td, $f)
        .asInstanceOf[PartialTransformerDefinition[
          From,
          To,
          CaseComputedPartial[Path.Match[Path.Root, Subtype], Tail],
          Flags
        ]]
    }

  def withConstructorImpl[
      From: Type,
      To: Type,
      Tail <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      Ctor: Type
  ](
      ti: Expr[PartialTransformerDefinition[From, To, Tail, Flags]],
      f: Expr[Ctor]
  )(using Quotes): Expr[PartialTransformerDefinition[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyConstructorType {
      [args <: ArgumentLists] =>
        (_: Type[args]) ?=>
          '{
            WithRuntimeDataStore
              .update($ti, $f)
              .asInstanceOf[PartialTransformerDefinition[From, To, Constructor[args, Path.Root, Tail], Flags]]
        }
    }(f)

  def withConstructorPartialImpl[
      From: Type,
      To: Type,
      Tail <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      Ctor: Type
  ](
      ti: Expr[PartialTransformerDefinition[From, To, Tail, Flags]],
      f: Expr[Ctor]
  )(using Quotes): Expr[PartialTransformerDefinition[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyConstructorType {
      [args <: ArgumentLists] =>
        (_: Type[args]) ?=>
          '{
            WithRuntimeDataStore
              .update($ti, $f)
              .asInstanceOf[PartialTransformerDefinition[From, To, ConstructorPartial[args, Path.Root, Tail], Flags]]
        }
    }(f)

  def buildTransformer[
      From: Type,
      To: Type,
      Tail <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      ImplicitScopeFlags <: TransformerFlags: Type
  ](
      td: Expr[PartialTransformerDefinition[From, To, Tail, Flags]]
  )(using Quotes): Expr[PartialTransformer[From, To]] =
    TransformerMacros.derivePartialTransformerWithConfig[From, To, Tail, Flags, ImplicitScopeFlags](td)
}
