package com.example.http4sdemo.auth

import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import org.http4s.dsl.Http4sDsl
import org.http4s.server._
import org.http4s.util.CaseInsensitiveString
import org.http4s.{Request, _}

object Authentication extends Http4sDsl[IO]{

  case class AuthenticatedUser(id: Long, name: String)

  private [Authentication] def parseRequest(req: Request[IO]): Either[String, AuthenticatedUser] = {

    val maybeUser = for {
      // reading from queryParams
      id <- req.params.get("id")
      name <- req.params.get("name")

      // reading from headers
      token <- req.headers.get(CaseInsensitiveString("token"))
    } yield AuthenticatedUser(id.toLong, s"$name (your token is: ${token.value})")

    maybeUser.toRight("Auth failed, sorry about that")
  }

  val onRequest: Kleisli[IO, Request[IO], Either[String, AuthenticatedUser]] =
    Kleisli(req => IO(parseRequest(req)))

  val onFailure: AuthedService[String, IO] = Kleisli(req => OptionT.liftF(Forbidden.apply(req.authInfo)))

  val middleware = AuthMiddleware(onRequest, onFailure)
}
