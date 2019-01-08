package com.example.http4sdemo.services

import cats.effect.IO
import org.http4s.AuthedService
import com.example.http4sdemo.auth.Authentication._


class AuthenticatedService {

  val authedService: AuthedService[AuthenticatedUser, IO] =
    AuthedService {
      case GET -> Root / "welcome" as user => Ok(s"Welcome, ${user.name}")
    }

  val service = middleware(authedService)
}
