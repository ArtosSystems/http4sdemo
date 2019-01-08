package com.example.http4sdemo.repos

import cats.effect.IO
import com.example.http4sdemo.services.HelloWorldService.ComplexGreeting

import scala.concurrent.Future
import scala.util.Random

class GreetingRepository{

  val random = Random

  def saveOne(greet: ComplexGreeting) = {

    IO.fromFuture(IO(Future.successful{random.nextInt(10)}))
  }
}
