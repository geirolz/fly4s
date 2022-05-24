package com.geirolz.macros.fluentcopy

import scala.annotation.{compileTimeOnly, StaticAnnotation}
import scala.reflect.api.Trees
import scala.reflect.macros.whitebox

object FluentCopyMacros {

  @compileTimeOnly("enable macro paradise")
  class FluentCopy extends StaticAnnotation {
    def macroTransform(annottees: Any*): Any = macro Macro.impl
  }

  object Macro {
    def impl(c: whitebox.Context)(annottees: c.Tree*): c.Expr[Any] = {
      import c.universe.*

      def extractClassNameAndFields(classDecl: ClassDef): (c.TypeName, List[Trees#Tree]) =
        try {
          val q"case class $className(..$fields) extends ..$_ { ..$_ }" = classDecl
          (className, fields.toList)
        } catch {
          case _: MatchError =>
            c.abort(c.enclosingPosition, "Annotation is only supported on case class")
        }

      def modifiedCompanion(
        compDeclOpt: Option[ModuleDef],
        toAdd: c.Tree,
        className: TypeName
      ): c.universe.Tree = {
        compDeclOpt map { compDecl =>
          val q"object $obj extends ..$bases { ..$body }" = compDecl
          q"""
          object $obj extends ..$bases {
            ..$body
            $toAdd
          }
        """
        } getOrElse {
          // Create a companion object with the formatter
          q"object ${className.toTermName} { $toAdd }"
        }
      }

      def implicitFluentCopyOps(className: c.TypeName, fields: List[Trees#Tree]): c.Tree = {

        val newMethods = fields.map { case q"$_ val $tname: $tpt = $_" =>
          val methodName = TermName(s"with${tname.toString().capitalize}")
          q"def $methodName($tname: $tpt): $className = i.copy($tname = $tname)"
        }

        val opsName: c.universe.TypeName = TypeName(s"${className.toTermName}FluentConfigOps")
        q"""  
          implicit class $opsName(i: $className){
            ..$newMethods
          }
         """
      }

      def modifiedDeclaration(
        classDecl: ClassDef,
        compDeclOpt: Option[ModuleDef] = None
      ): c.Expr[Any] = {
        val (className, fields) = extractClassNameAndFields(classDecl)
        val ops: c.Tree         = implicitFluentCopyOps(className, fields)
        val compDecl: c.Tree    = modifiedCompanion(compDeclOpt, ops, className)

        // Return both the class and companion object declarations
        c.Expr(q"""
        $classDecl
        $compDecl
      """)
      }

      annottees match {
        case (classDecl: ClassDef) :: Nil => modifiedDeclaration(classDecl)
        case (classDecl: ClassDef) :: (compDecl: ModuleDef) :: Nil =>
          modifiedDeclaration(classDecl, Some(compDecl))
        case _ => c.abort(c.enclosingPosition, "Invalid annottee")
      }
    }
  }
}
