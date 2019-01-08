package com.example.http4sdemo.routing

import java.time.LocalDate

import scala.util.Try

object PathExtractors {

  object LocalDateVar {
    def unapply(str: String): Option[LocalDate] = {
      Try(LocalDate.parse(str)).map(date => Some(date)).getOrElse(None)
    }
  }
}
