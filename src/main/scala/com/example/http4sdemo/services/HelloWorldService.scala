package com.example.http4sdemo.services

import cats.effect.{Effect, IO}
import com.example.http4sdemo.repos.GreetingRepository
import com.example.http4sdemo.services.HelloWorldService.{ComplexGreeting, GreetingResponse}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Json, _}
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

// asJson implicit
import io.circe.syntax._

class HelloWorldService[F[_]: Effect](repo: GreetingRepository) extends Http4sDsl[IO] {

  //implicit val dec = CirceEntityDecoder.circeEntityDecoder[F, ComplexGreeting]

  val service: HttpService[IO] = {
    HttpService[IO] {
      case GET -> Root / "hello" / name =>
        Ok(Json.obj("message" -> Json.fromString(s"Hello, ${name}, how's it going today")))

      case GET -> Root / "bye" / name =>
        Ok(GreetingResponse(name).asJson)

      case GET -> Root / "complex" / name / IntVar(age) =>
        Ok(ComplexGreeting(name, age).asJson)

      case req @ POST -> Root / "api" / "greetings" => {

        for{
          greet <- req.decodeJson[ComplexGreeting]
          id <- repo.saveOne(greet)
          response <- Ok(id.asJson)
        } yield response
      }
    }
  }
}

object HelloWorldService{

  case class GreetingResponse(msg: String)
  case class ComplexGreeting(msg: String, age: Int)

  case object GreetingResponse{
    implicit val encoder: Encoder[GreetingResponse] = deriveEncoder
  }

  case object ComplexGreeting{
    implicit val encoder: Encoder[ComplexGreeting] = deriveEncoder
    implicit val decoder: Decoder[ComplexGreeting] = deriveDecoder
  }
}

