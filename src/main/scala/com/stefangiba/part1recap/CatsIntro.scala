package com.stefangiba.part1recap

object CatsIntro {
  // Eq
  // val comparison = 2 == "a string" // does not compile

  // part 1 - type class import
  import cats.Eq

  // part 2 - import TC instances for the types you need
  import cats.instances.int.*

  // part 3 - use the TC API
  val intEquality        = Eq[Int]
  val typeSafeComparison = intEquality.eqv(1, 2)
  // val unsafeComparison   = intEquality.eqv(1, "a string") // does not compile

  // part 4 - use extension methods (if applicable)
  import cats.syntax.eq.*
  val anotherTypeSafeComparison = 1 === 2 // false
  val neqComparison             = 1 =!= 2 // true
  // val invalidComparison = 2 === "a string" // does not compile
  // externsion methods are only visible in the presence of the right TC instance

  // part 5 - extending the TC operations to composite types, e.g. lists
  import cats.instances.list.*
  val listComparison = List(2) === List(3)

  // part 6 - create a TC instance for custom types
  case class ToyCar(model: String, price: Double)
  object ToyCar {
    given toyCarEq: Eq[ToyCar] = Eq.instance[ToyCar] { (car1, car2) =>
      car1.price === car2.price && car1.model === car2.model
    }
  }

  val compareTwoToyCars =
    ToyCar("Ferrari", 1000) === ToyCar("Lamborghini", 1000)
}
