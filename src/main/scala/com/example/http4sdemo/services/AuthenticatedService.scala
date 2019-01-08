package com.example.http4sdemo.services

import cats.effect.IO
import com.example.http4sdemo.auth.{Authentication, User}
import org.http4s.AuthedService


class AuthenticatedService {

  import Authentication._

  val authedService: AuthedService[User, IO] =
    AuthedService {
      case GET -> Root / "welcome" as user => Ok(s"Welcome, ${user.name}")
    }

  val service = middleware(authedService)
}
