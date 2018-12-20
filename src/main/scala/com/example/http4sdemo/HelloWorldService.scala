package com.example.http4sdemo

import cats.effect.Effect
import io.circe.{Json, _}
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import HelloWorldService.{ComplexGreeting, GreetingResponse}

// asJson implicit
import io.circe.syntax._

class HelloWorldService[F[_]: Effect] extends Http4sDsl[F] {

  val service: HttpService[F] = {
    HttpService[F] {
      case GET -> Root / "hello" / name =>
        Ok(Json.obj("message" -> Json.fromString(s"Hello, ${name}, how's it going today")))

      case GET -> Root / "bye" / name =>
        Ok(GreetingResponse(name).asJson)

      case GET -> Root / "complex" / name =>
        Ok(ComplexGreeting(name, 20).asJson)
    }
  }
}



object HelloWorldService{

  case class GreetingResponse(msg: String)
  case class ComplexGreeting(msg: String, age: Int)

  case object GreetingResponse{
    implicit val encoder: Encoder[GreetingResponse] = deriveEncoder //Encoder.encodeString.contramap(_.msg)
  }

  case object ComplexGreeting{
    implicit val encoder: Encoder[ComplexGreeting] = deriveEncoder
  }
}

