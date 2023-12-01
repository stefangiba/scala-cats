package com.stefangiba.part2abstractMath

object UsingMonads {
  import cats.Monad
  import cats.instances.list.*

  val monadList    = Monad[List]
  val simpleList   = monadList.pure(3)
  val extendedList = monadList.flatMap(simpleList)(x => List(x, x + 1))
  // applicable to Option, Try, Future

  // either is also a monad
  val manualEither: Either[String, Int] = Right(42)
  type LoadingOr[T] = Either[String, T]
  type ErrorOr[T]   = Either[Throwable, T]

  import cats.instances.either.*
  val loadingMonad = Monad[LoadingOr]
  val either       = loadingMonad.pure(42) // LoadingOr[Int] = Right(42)
  val modifiedLoading = loadingMonad.flatMap(either)(x =>
    Right(x + 1)
  ) // LoadingOr[Int] = Right(43)

  // imaginary online store
  case class OrderStatus(orderId: Long, status: String)
  def getOrderStatus(orderId: Long): LoadingOr[OrderStatus] =
    Right(OrderStatus(orderId, "Ready"))
  def trackLocation(orderStatus: OrderStatus): LoadingOr[String] =
    if orderStatus.orderId > 1000 then
      Left("Not available yet, refreshing data...")
    else Right("London, UK")

  val orderId = 457L
  val orderLocation =
    loadingMonad.flatMap(getOrderStatus(orderId))(trackLocation)

  // use extension methods
  import cats.syntax.flatMap.*
  import cats.syntax.functor.*

  val orderLocationBetter = getOrderStatus(orderId).flatMap(trackLocation)
  val orderLocationFor = for {
    orderStatus <- getOrderStatus(orderId)
    location    <- trackLocation(orderStatus)
  } yield location

  // TODO: the service layer API of a web app
  case class Connection(host: String, port: String)
  val config = Map(
    "host" -> "localhost",
    "port" -> "4040"
  )

  trait HttpService[M[_]] {
    def getConnection(cfg: Map[String, String]): M[Connection]
    def issueRequest(conn: Connection, payload: String): M[String]
  }

  def getResponse[M[_]: Monad](
      service: HttpService[M],
      payload: String
  ): M[String] = for {
    conn     <- service.getConnection(config)
    response <- service.issueRequest(conn, payload)
  } yield response

  /*
    Requirements:
      - if the host and port are found in the configuration map, then we'll return a M containing a connection with those values,
      otherwise the method will fail, according to the logic of type M
      (for Try it will return a Failure, for Option it will return None, for Future it will be a failed Future, for Either it will be Left)
      - the issueRequest method returns a M containing the string: "request (payload) has been accepted", if the payload is less than 100 characters,
      otherwise the method will fail, according to the logic of type M

      TODO: provide a real implementation of HttpService using Try, Option, Future, Either
   */

  object OptionHttpService extends HttpService[Option] {
    def getConnection(cfg: Map[String, String]): Option[Connection] = for {
      host <- cfg.get("host")
      port <- cfg.get("port")
    } yield Connection(host, port)

    def issueRequest(conn: Connection, payload: String): Option[String] =
      if payload.length >= 100 then None
      else Some(s"Request ($payload) has been accepted")
  }

  object LoadingOrHttpService extends HttpService[LoadingOr] {
    def getConnection(cfg: Map[String, String]): LoadingOr[Connection] =
      if cfg.contains("host") && cfg.contains("port") then
        Right(Connection(cfg("host"), cfg("port")))
      else Left("Connection could not be established: invalid configuration!")

    def issueRequest(conn: Connection, payload: String): LoadingOr[String] =
      if payload.length >= 100 then Left("Payload is too long!")
      else Right(s"Request ($payload) has been accepted")
  }

  def main(args: Array[String]): Unit = {
    val responseOption = for {
      conn <- OptionHttpService.getConnection(config)
      response <- OptionHttpService.issueRequest(
        conn,
        "Hello from Option HTTP service"
      )
    } yield response

    println(responseOption)

    val stringOrResponse = for {
      conn <- LoadingOrHttpService.getConnection(config)
      response <- LoadingOrHttpService.issueRequest(
        conn,
        "Hello from LoadingOr HTTP service"
      )
    } yield response

    println(stringOrResponse)

    println(getResponse(OptionHttpService, "HelloOption"))
    println(getResponse(LoadingOrHttpService, "HelloLoadingOr"))
  }
}
