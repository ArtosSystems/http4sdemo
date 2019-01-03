package com.example.http4sdemo

import cats.effect.{Effect, IO}
import fs2.StreamApp
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext

object HelloWorldServer extends StreamApp[IO] {
  import scala.concurrent.ExecutionContext.Implicits.global

  def stream(args: List[String], requestShutdown: IO[Unit]) = ServerStream.stream[IO]
}

object ServerStream {

  def helloWorldService[F[_]: Effect] = new HelloWorldService[IO]().service

  def stream[F[_]: Effect](implicit ec: ExecutionContext) =
    BlazeBuilder[IO]
      .bindHttp(8085, "0.0.0.0")
      .mountService(helloWorldService, "/")
      .serve
}
