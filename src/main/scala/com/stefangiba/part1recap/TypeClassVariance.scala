package com.stefangiba.part1recap

import com.stefangiba.part1recap.Essentials.Animal

object TypeClassVariance {
  import cats.Eq
  import cats.instances.int.*    // Eq[Int] TC instance
  import cats.instances.option.* // construct a Eq[Option[Int]] TC instance
  import cats.syntax.eq.*

  val comparison = Option(2) === Option(3)
  // val invalidComparison = Some(2) === None // Eq[Some[Int]] not found

  // variance
  class Animal
  class Cat extends Animal

  // covariant type: subtyping is propagated to the generic type
  class Cage[+T]
  val cage: Cage[Animal] =
    new Cage[Cat] // Cat <: Animal, so Cage[Cat] <: Cage[Animal]

  // contravariant type: subtyping is propagated BACKWARDS to the generic type
  class Vet[-T]
  val vet: Vet[Cat] =
    new Vet[Animal] // Cat <: Animal, so Vet[Animal] <: Vet[Cat]

  // rule of thumb: "HAS a T" = covariant, "ACTS on T" = contravariant
  // variance affect how TC instances are being fetched

  // contravariant TC
  trait SoundMaker[-T]
  given animalSoundMaker: SoundMaker[Animal] with {}

  def makeSound[T](implicit soundMaker: SoundMaker[T]): Unit =
    println("makes sound")

  makeSound[Animal] // ok - TC instance defined above
  makeSound[Cat]    // ok - TC instance for Animal is also applicable to Cats

  // rule 1: contravariant TCs can use the superclass instances if nothing is available strictly for that type

  // has implications for subtypes
  given optionSoundMaker: SoundMaker[Option[Int]] with {}
  makeSound[Option[Int]] // ok - TC instance defined above
  makeSound[Some[Int]]

  // covariant TC
  trait AnimalShow[+T] {
    def show: String
  }
  given generalAnimalShow: AnimalShow[Animal] with {
    def show: String = "animal everywhere"
  }
  given catsShow: AnimalShow[Cat] with {
    override def show: String = "so many cats!"
  }
  def organizeShow[T](using event: AnimalShow[T]): String = event.show
  // rule 2: covariant TCs wil always use the more specific TC instance for that type

  // rule 3: you can't have both
  // Cats uses INVARIANT TCs

  def main(args: Array[String]): Unit = {
    // ok - the compiler will inject catsShow as implicit
    println(organizeShow[Cat])

    // also picks catsShow
    println(
      organizeShow[Animal]
    )
  }
}
