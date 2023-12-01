package com.stefangiba.part2abstractMath

import scala.util.Try

object Functors {
  val modifiedList   = List(1, 2, 3).map(_ + 1)
  val modifiedOption = Option(1).map(_ + 1)
  val modifiedTry    = Try(42).map(_ + 1)

  trait MyFunctor[F[_]] {
    def map[A, B](initialValue: F[A])(f: A => B): F[B]
  }

  // Cats Functor
  import cats.Functor
  import cats.instances.list._
  val listFunctor        = Functor[List]
  val incrementedNumbers = listFunctor.map(List(1, 2, 3))(_ + 1)

  import cats.instances.option.*
  val optionFunctor     = Functor[Option]
  val incrementedOption = optionFunctor.map(Option(1))(_ + 1)

  import cats.instances.try_.*
  val incrementedTry = Functor[Try].map(Try(42))(_ + 1)

  // Functors become important when generalizing an API
  def do10xList(list: List[Int]): List[Int]         = list.map(_ * 10)
  def do10xOption(option: Option[Int]): Option[Int] = option.map(_ * 10)
  def do10xTry(attempt: Try[Int]): Try[Int]         = attempt.map(_ * 10)

  def do10x[F[_]](container: F[Int])(using functor: Functor[F]): F[Int] =
    functor.map(container)(_ * 10)

  // TODO 1: define your own functor for a binary tree
  // hint: define an object which extends Functor[Tree]
  sealed trait Tree[+T]
  case class Leaf[+T](value: T)                                  extends Tree[T]
  case class Branch[+T](value: T, left: Tree[T], right: Tree[T]) extends Tree[T]

  object Tree {
    def leaf[T](value: T): Tree[T] = Leaf(value)
    def branch[T](value: T, left: Tree[T], right: Tree[T]): Tree[T] =
      Branch(value, left, right)
  }

  given treeFunctor: Functor[Tree] with {
    override def map[A, B](initialValue: Tree[A])(f: A => B): Tree[B] =
      initialValue match {
        case Leaf(value) => Leaf(f(value))
        case Branch(value, l, r) =>
          Branch(f(value), map(l)(f), map(r)(f))
      }
  }

  // extension method - map
  import cats.syntax.functor.*
  val tree: Tree[Int] = Branch(1, Leaf(2), Leaf(3))
  val modifiedTree    = tree.map(_ * 10)

  // TODO 2: write a shorter do10x method using extension methods
  def do10xShorter[F[_]: Functor](container: F[Int]): F[Int] =
    container.map(_ * 10)

  def main(args: Array[String]): Unit = {
    println(do10x(List(1, 2, 3)))
    println(do10x(Option(2)))
    println(do10x(Try(42)))
    // needs explicit type specified, since Cats TCs are invariant, so no TC instance for Branch is found in scope
    // solution, use smart constructors
    println(do10x(Tree.branch(1, Tree.leaf(2), Tree.leaf(3))))
  }
}
