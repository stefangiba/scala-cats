package com.stefangiba.part1recap

object Implicits {
  // implicit classes
  case class Person(name: String) {
    def greet: String = s"Hi, my name is $name"
  }

  // exetension methods
  implicit class ImpersonableString(str: String) {
    def greet: String = Person(str).greet
  }
  val greeting = "Stefan".greet

  // importing implicit conversions in scope
  import scala.concurrent.duration._
  val oneSec = 1.second

  // implicit arguments and values
  def increment(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount: Int             = 10
  val incremented2                            = increment(2)

  def multiply(x: Int)(implicit times: Int) = x * times
  val times2                                = multiply(2)

  // more complex example
  trait JSONSerializer[T] {
    def toJson(value: T): String
  }

  def listToJson[T](list: List[T])(implicit
      serializer: JSONSerializer[T]
  ): String =
    s"[${list.map(serializer.toJson).mkString(", ")}]"

  given personSerializer: JSONSerializer[Person] with {
    override def toJson(value: Person): String =
      s"""{
        |"name": "${value.name}"
        |}""".stripMargin
  }
  val peopleJson = listToJson(List(Person("Stefan"), Person("Gabriel")))
  // implicit argument is used to PROVE THE EXISTENCE of a type

  // implicit methods
  // implicit def oneArgCaseClassSerializer[T <: Product]: JSONSerializer[T] =
  //   new JSONSerializer[T] {
  //     override def toJson(value: T): String =
  //       s"""{ "${value.productElementName(0)}": "${value.productElement(
  //           0
  //         )}" }""".stripMargin
  //   }

  given caseClassSerializer[T <: Product]: JSONSerializer[T] with {
    override def toJson(value: T): String =
      s"""{ "${value.productElementName(0)}": "${value.productElement(
          0
        )}" }""".stripMargin
  }

  case class Cat(catName: String)
  val catsToJson = listToJson(List(Cat("Tom"), Cat("Garfield")))
  // implicit methods are used to PROVE THE EXISTENCE of a type

  def main(args: Array[String]): Unit = {
    println(summon[JSONSerializer[Cat]].toJson(Cat("Garfield")))
    println(summon[JSONSerializer[Person]].toJson(Person("David")))
  }
}
