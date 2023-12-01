package com.stefangiba.part2abstractMath

import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import scala.concurrent.Future

object Monads {
  // lists
  val numbersList = List(1, 2, 3)
  val charsList   = List('a', 'b', 'c')

  val combinationsList =
    numbersList.flatMap(number => charsList.map(char => (number, char)))
  val combinationsListFor = for {
    number <- numbersList
    char   <- charsList
  } yield (number, char) // identical to the above

  // options
  val numberOption = Option(2)
  val charOption   = Option('b')
  val combinationsOption =
    numberOption.flatMap(number => charOption.map(char => (number, char)))
  val combinationsOptionFor = for {
    number <- numberOption
    char   <- charOption
  } yield (number, char)

  // futures
  given ec: ExecutionContext =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(8))
  val numberFuture = Future(42)
  val charFuture   = Future('Z')
  val combinationsFuture =
    numberFuture.flatMap(number => charFuture.map(char => (number, char)))
  val combinationsFutureFor = for {
    number <- numberFuture
    char   <- charFuture
  } yield (number, char)

  /*
    Pattern:
      - wrapping a value into a MONADIC value
      - the flatMap mechanism

    MONADS
   */

  trait MyMonad[M[_]] {
    def pure[A](a: A): M[A]
    def flatMap[A, B](ma: M[A])(f: A => M[B]): M[B]
    def map[A, B](ma: M[A])(f: A => B): M[B] = flatMap(ma)(a => pure(f(a)))
  }

  // Cats Monad
  import cats.Monad
  import cats.instances.option.* // includes Monad[Option]
  val optionMonad       = Monad[Option]
  val option            = optionMonad.pure(42)
  val transformedOption = optionMonad.flatMap(option)(x => Option(x + 1))

  import cats.instances.list.* // includes Monad[List]
  val listMonad       = Monad[List]
  val list            = listMonad.pure(3) // List(3)
  val transformedList = listMonad.flatMap(list)(x => List(x, x + 1))

  import cats.instances.future.*
  val futureMonad = Monad[Future] // requires an implicit ExecutionContext
  val future      = futureMonad.pure(42)
  val transformedFuture = futureMonad.flatMap(future)(x =>
    Future(x + 1)
  ) // future that will end up with Success(43)

  // generalized API
  def getPairs[M[_], A, B](ma: M[A], mb: M[B])(using
      monad: Monad[M]
  ): M[(A, B)] = monad.flatMap(ma)(a => monad.map(mb)(b => (a, b)))

  // extension methods - weirder imports - pure, flatMap

  import cats.syntax.applicative.* // pure is here
  val oneOption = 1.pure[Option] // implicit Monad[Option] will be used
  val oneList   = 1.pure[List]

  import cats.syntax.flatMap.* // flatMap is here
  val oneOptionTransformed = oneOption.flatMap(x => (x + 1).pure[Option])

  // Monads extend Functors
  import cats.syntax.functor.* // map is here
  val oneOptionMapped    = oneOption.map(_ + 1)
  val oneOptionMapped_v2 = Monad[Option].map(oneOption)(_ + 1)

  // for-comprehensions
  val composedOptionFor = for {
    one <- 1.pure[Option]
    two <- 2.pure[Option]
  } yield one + two

  def getPairs_v2[M[_]: Monad, A, B](ma: M[A], mb: M[B]): M[(A, B)] = for {
    a <- ma
    b <- mb
  } yield (a, b)

  def main(args: Array[String]): Unit = {
    println(getPairs_v2(numbersList, charsList))
    println(getPairs_v2(numberOption, charOption))
    getPairs_v2(numberFuture, charFuture).foreach(println)
  }
}
