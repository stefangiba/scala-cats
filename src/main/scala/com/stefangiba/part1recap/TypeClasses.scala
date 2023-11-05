package com.stefangiba.part1recap

object TypeClasses {
  case class Person(name: String, age: Int)

  // part 1 - type class definition
  trait JSONSerializer[T] {
    def toJson(value: T): String
  }

  // part 2 - create implicit type class instances
  implicit object StringSerializer extends JSONSerializer[String] {
    override def toJson(value: String): String = s"\"$value\""
  }

  given intSerializer: JSONSerializer[Int] = new JSONSerializer[Int] {
    override def toJson(value: Int): String = value.toString
  }

  given personSerializer: JSONSerializer[Person] = new JSONSerializer[Person] {
    override def toJson(value: Person): String = s"""
    |{ "name": "${value.name}", "age": ${value.age} }
    |""".stripMargin.trim
  }

  given listSerializer[T](using
      serializer: JSONSerializer[T]
  ): JSONSerializer[List[T]] with {
    override def toJson(list: List[T]): String =
      list.map(serializer.toJson).mkString("[", ", ", "]")
  }

  // part 3 - offer some API
  def convertListToJson[T](list: List[T])(using
      serializer: JSONSerializer[T]
  ): String =
    list.map(serializer.toJson).mkString("[", ",", "]")

  // part 4 - extending the existing types via extension methods
  object JSONSyntax {
    extension [T](value: T) {
      def toJson(using serializer: JSONSerializer[T]): String =
        serializer.toJson(value)
    }
  }

  def main(args: Array[String]): Unit = {
    println(convertListToJson(List(Person("Alice", 23), Person("Xavier", 45))))
    // bob.toJson
    import JSONSyntax._
    println(Person("Bob", 45).toJson)
    println(List(Person("Alice", 23), Person("Xavier", 45)).toJson)
  }
}
