package com.example.http4sdemo

import cats.effect.{Effect, IO}
import com.example.http4sdemo.repos.GreetingRepository
import com.example.http4sdemo.services.{AuthenticatedService, HelloWorldService}
import fs2.StreamApp
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext

object HelloWorldServer extends StreamApp[IO] {
  import scala.concurrent.ExecutionContext.Implicits.global

  def stream(args: List[String], requestShutdown: IO[Unit]) = ServerStream.stream[IO]
}

object ServerStream {

  def helloWorldService[F[_]: Effect] = new HelloWorldService[IO](new GreetingRepository).service

  def authenticatedService = new AuthenticatedService().service

  def stream[F[_]: Effect](implicit ec: ExecutionContext) =
    BlazeBuilder[IO]
      .bindHttp(8085, "0.0.0.0")
      .mountService(helloWorldService, "/")
      .mountService(authenticatedService, "/auth")
      .serve
}
