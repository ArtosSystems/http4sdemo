package com.example.http4sdemo

import cats.effect.IO
import com.example.http4sdemo.HelloWorldService.ComplexGreeting

import scala.concurrent.Future
import scala.util.Random

class GreetingRepository{
//  def saveOne(greet: ComplexGreeting) = {
//    Future.successful(Math.random().toInt)
//  }

  val random = Random

  def saveOne(greet: ComplexGreeting) = {

    IO.fromFuture(IO(Future.successful{random.nextInt(10)}))
  }
}
