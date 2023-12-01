package com.stefangiba.part2abstractMath

object Monoids {
  // import cats.Semigroup
  import cats.instances.int.*
  import cats.syntax.semigroup.* // import the |+| extension method

  val numbers = (1 to 1000).toList
  // |+| is always associative
  val sumLeft  = numbers.foldLeft(0)(_ |+| _)
  val sumRight = numbers.foldRight(0)(_ |+| _)

  // define a general API
  // def combineFold[A](list: List[A])(using semigroup: Semigroup[A]): A =
  //   list.foldLeft( /* WHAT?! */ )(_ |+| _)
  // not working, don't know the zero value

  // MONOIDS
  import cats.Monoid
  val intMonoid  = Monoid[Int]
  val combineInt = intMonoid.combine(1, 2)
  val zero       = intMonoid.empty // 0

  import cats.instances.string.*
  val emptyString   = Monoid[String].empty // ""
  val combineString = Monoid[String].combine("Hello", "World")

  import cats.instances.option.*
  val emptyOption = Monoid[Option[Int]].empty // None
  val combineOption =
    Monoid[Option[Int]].combine(Some(1), Option.empty) // Some(2)
  val combineOption2 = Monoid[Option[Int]].combine(Some(1), Some(2)) // Some(3)

  // extension methods for Monoids - |+|
  import cats.syntax.monoid.* // either this one or cats.syntax.semigroup.*
  val combinedOptionFancy = Option(3) |+| Option(4)

  // TODO 1: implement combineFold
  def combineFold[A](list: List[A])(using monoid: Monoid[A]): A =
    list.foldLeft(monoid.empty)(_ |+| _)

  // TODO 2: combine a list of phonebooks as Map[String, Int]
  val phoneBooks = List(
    Map(
      "Alice" -> 235,
      "Bob"   -> 647
    ),
    Map(
      "Charlie" -> 372,
      "Daniel"  -> 889
    ),
    Map(
      "Stefan" -> 125
    )
  )

  import cats.instances.map.*
  val massivePhoneBook = combineFold(phoneBooks)

  // TODO 3 - shopping cart and online stores with Monoids
  case class ShoppingCart(items: List[String], total: Double)
  given shoppingCartMonoid: Monoid[ShoppingCart] =
    Monoid.instance(
      ShoppingCart(List.empty, 0),
      (sc1, sc2) => ShoppingCart(sc1.items ++ sc2.items, sc1.total + sc2.total)
    )

  def checkout(shoppingCarts: List[ShoppingCart]): ShoppingCart = combineFold(
    shoppingCarts
  )

  def main(args: Array[String]): Unit = {
    println(sumLeft)
    println(sumRight)
    println(combineFold(numbers))
    println(combineFold(List("a", "b", "c")))
    println(massivePhoneBook)
    println(
      checkout(
        List(
          ShoppingCart(List("a", "b"), 1),
          ShoppingCart(List("c", "d"), 2),
          ShoppingCart(List("e", "f"), 3)
        )
      )
    )
  }
}
