package com.example.http4sdemo

import cats.effect.{Effect, IO}
import com.example.grpc.helloworld.HelloRequest
import com.example.http4sdemo.HelloWorldService.{ComplexGreeting, GreetingResponse}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Json, _}
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

import scala.concurrent.ExecutionContext

// asJson implicit
import io.circe.syntax._

import JsonProtoUtils.jsonOf

class HelloWorldService[F[_]: Effect] extends Http4sDsl[IO] {

  implicit val ec = ExecutionContext.global
  val repo = new GreetingRepository

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
          response <- Created(id.asJson)
        } yield response
      }
      case req @ POST -> Root / "proto" => {
        for {
          hello <- req.as[HelloRequest]
          u = println("HELLO", hello)
          response <- Ok(hello.name.asJson)
        } yield response
      }
    }
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

import cats.effect.IO
import io.circe.{Json, Printer}
import org.http4s.circe.CirceInstances
import org.http4s.{DecodeResult, EntityDecoder, InvalidMessageBodyFailure}
import scalapb.{GeneratedMessage, GeneratedMessageCompanion, Message}

object JsonProtoUtils {
  val circeInstances = CirceInstances.withPrinter(Printer.spaces2)
  import circeInstances._

  implicit def jsonOf[
  T <: GeneratedMessage with Message[T]: GeneratedMessageCompanion]
  : EntityDecoder[IO, T] =
    jsonDecoder[IO].flatMapR { json =>
      scalapb_circe.JsonFormat
        .protoToDecoder[T]
        .decodeJson(json)
        .fold(
          failure =>
            DecodeResult.failure(
              InvalidMessageBodyFailure(s"Could not decode JSON: $json",
                Some(failure))),
          DecodeResult.success(_)
        )
    }

  implicit def entityEncoder[
  T <: GeneratedMessage with Message[T]: GeneratedMessageCompanion] = {
    jsonEncoderWithPrinterOf[IO, Json](Printer.spaces2)
      .contramap(scalapb_circe.JsonFormat.toJson[T])
  }
}

//object JsonProtoUtils extends JsonProtoUtils

