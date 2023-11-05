package com.stefangiba.part1recap

import scala.util.Try
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

object Essentials {
  // values
  val boolean: Boolean = true

  // expressions are EVALUATED to a value
  val ifExpression = if (2 > 3) 1 else 0

  // instructions vs expressions
  val unit = println("Hello, Scala!") // Unit = "void" in other languages

  // OOP
  class Animal
  class Cat extends Animal
  trait Carnivore {
    def eat(animal: Animal): Unit
  }

  // inheritance model: extend <= 1 class, but inherit from >= 0 traits
  class Crocodile extends Animal with Carnivore {
    override def eat(animal: Animal): Unit = println("Crunch!")
  }

  // singleton
  object MySingleton // singleton pattern in one line

  // companions
  object Carnivore // companion object of the class Carnivore

  // generics
  class MyList[A]

  // method notation
  val three        = 1 + 2
  val anotherThree = 1.+(2)

  // functional programming
  val incrementer: Int => Int = x => x + 1
  val incremented             = incrementer(45)

  // map, flatMap, filter
  val processedList = List(1, 2, 3).map(incrementer)
  val longerList    = List(1, 2, 3).flatMap(x => List(x, x + 1))

  // for-comprehensions
  val checkerboard =
    List(1, 2, 3).flatMap(n => List('a', 'b', 'c').map(c => (n, c)))
  val anotherCheckerboard = for {
    n <- List(1, 2, 3)
    c <- List('a', 'b', 'c')
  } yield (n, c) // equivalent to the above

  // options and try
  val option: Option[Int]        = Option( /* something that might be null*/ 3)
  val doubledOption: Option[Int] = option.map(_ * 2)

  val attempt         = Try( /* something that might throw */ 42) // Success(42)
  val modifiedAttempt = attempt.map(_ + 10)

  // pattern matching
  val unknown: Any = 45
  val ordinal = unknown match {
    case 1 => "first"
    case 2 => "second"
    case _ => "unknown"
  }

  val optionDescription = option match
    case None        => "The option is empty"
    case Some(value) => s"The option is not empty: $value"

  // futures
  given ec: ExecutionContext =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(8))

  val future = Future(42)

  // wait for completion
  future.onComplete {
    case Success(value)     => println(s"The async meaning of life is $value")
    case Failure(exception) => println(s"Meaning of life failed: $exception")
  }

  // map a future
  val anotherFuture = future.map(_ + 1)

  // partial functions
  val partialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 65
    case 5 => 999
  }

  // some more advanced stuff
  trait HigherKindedType[F[_]]
  trait SequenceChecker[F[_]] {
    def isSequential: Boolean
  }

  val listChecker = new SequenceChecker[List] {
    override def isSequential: Boolean = true
  }

  def main(args: Array[String]): Unit = {}
}
