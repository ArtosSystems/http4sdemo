package com.example.http4sdemo

import cats.effect.{Effect, IO}
import io.circe.{Json, _}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import HelloWorldService.{ComplexGreeting, GreetingResponse}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import io.circe.generic.auto._



// asJson implicit
import io.circe.syntax._

class HelloWorldService[F[_]: Effect] extends Http4sDsl[F] {

  implicit val ec = ExecutionContext.global
  val repo = new GreetingRepository

  implicit val dec = CirceEntityDecoder.circeEntityDecoder[F, ComplexGreeting]


  val service: HttpService[F] = {
    HttpService[F] {
      case GET -> Root / "hello" / name =>
        Ok(Json.obj("message" -> Json.fromString(s"Hello, ${name}, how's it going today")))

      case GET -> Root / "bye" / name =>
        Ok(GreetingResponse(name).asJson)

      case GET -> Root / "complex" / name / IntVar(age) =>
        Ok(ComplexGreeting(name, age).asJson)

      case req @ POST -> Root / "api" / "greetings" => {

        req.decode[ComplexGreeting]{ greet =>

          repo.saveOne(greet).map(id => Ok(id.asJson))

        }

//        for {
//          // Decode a User request
//          greet <- req.as(jsonOf[ComplexGreeting])
//          id <- repo.saveOne(greet)
//          // Encode a hello response
//          resp <- Ok(id).asJson
//        } yield (resp)

      }

    }
  }
}


class GreetingRepository{
//  def saveOne(greet: ComplexGreeting) = {
//    Future.successful(Math.random().toInt)
//  }

  def saveOne[F[_]: Effect](greet: ComplexGreeting) = {
    //Effect[F](Math.random().toInt)

    Math.random().toInt
    IO.fromFuture(Future.successful(Math.random().toInt))
    //Future.successful(Math.random().toInt)
  }
}


object HelloWorldService{

  case class GreetingResponse(msg: String)
  case class ComplexGreeting(msg: String, age: Int)

  //implicit val encoder: Encoder[Int] = deriveEncoder

  case object GreetingResponse{
    implicit val encoder: Encoder[GreetingResponse] = deriveEncoder //Encoder.encodeString.contramap(_.msg)
  }

  case object ComplexGreeting{
    implicit val encoder: Encoder[ComplexGreeting] = deriveEncoder
    implicit val decoder: Decoder[ComplexGreeting] = deriveDecoder
  }
}

