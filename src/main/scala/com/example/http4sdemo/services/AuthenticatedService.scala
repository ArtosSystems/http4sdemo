package com.example.http4sdemo.services

import cats.effect.IO
import com.example.http4sdemo.auth.Authentication._
import com.example.http4sdemo.routing.PathExtractors.LocalDateVar
import io.circe.Json
import org.http4s.AuthedService
import org.http4s.circe._



class AuthenticatedService {

  val authedService: AuthedService[AuthenticatedUser, IO] =
    AuthedService {
      case GET -> Root / "welcome" as user => Ok(s"Welcome, ${user.name}")

      case GET -> Root / "date" / LocalDateVar(date) as user => {
        Ok(Json.obj("message" -> Json.fromString(s"Hello, ${user.name}, date: ${date}")))
      }
    }

  val service = middleware(authedService)
}
