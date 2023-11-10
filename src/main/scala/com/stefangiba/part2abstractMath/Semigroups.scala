package com.stefangiba.part2abstractMath

object Semigroups {
  // Semigroups COMBINE elements of the same type
  import cats.Semigroup
  import cats.instances.int.*

  val naturalIntSemigroup: Semigroup[Int] = Semigroup[Int]
  val intCombination                      = naturalIntSemigroup.combine(2, 46)

  import cats.instances.string.*
  val naturalStringSemigroup: Semigroup[String] = Semigroup[String]
  val stringCombination = naturalStringSemigroup.combine("I love ", "Cats")

  // specific API
  def reduceInts(list: List[Int]): Int =
    list.reduce(naturalIntSemigroup.combine)
  def reduceStrings(list: List[String]): String =
    list.reduce(naturalStringSemigroup.combine)

  // general API
  def reduceThings[T](list: List[T])(using semigroup: Semigroup[T]): T =
    list.reduce(semigroup.combine)

  // TODO 1: support a new type
  case class Expense(id: Long, amount: Double)
  given expenseSemigroup: Semigroup[Expense] =
    Semigroup.instance[Expense]((e1, e2) =>
      Expense(Math.max(e1.id, e2.id), e1.amount + e2.amount)
    )

  // extension methods from Semigroup - |+|
  import cats.syntax.semigroup.*
  val intSum = 2 |+| 3 // require the presence of an implicit Semigroup[Int]
  val stringConcat    = "We like " |+| "semigroups"
  val combinedExpense = Expense(1, 23.4) |+| Expense(2, 45.6)

  // TODO 2: implement reduceThings using extension methods
  def reduceThings2[T: Semigroup](list: List[T]): T =
    list.reduce(_ |+| _)

  def main(args: Array[String]): Unit = {
    println(intCombination)
    println(stringCombination)

    // specific API
    val numbers = (1 to 10).toList
    println(reduceInts(numbers))
    val strings = List("I'm ", "starting ", "to ", "like ", "semigroups")
    println(reduceStrings(strings))

    // general API
    println(
      reduceThings(numbers)
    ) // compiler injects the implicit Semigroup[Int]
    println(reduceThings(strings))

    import cats.instances.option.*
    val numberOptions = numbers.map(Option(_))
    println(reduceThings(numberOptions))
    val stringOptions = strings.map(Option(_))
    println(reduceThings(stringOptions))

    val expenses = List(Expense(1, 23.4), Expense(2, 45.6), Expense(3, 46))
    println(reduceThings(expenses))

    println(reduceThings2(expenses))
  }
}
