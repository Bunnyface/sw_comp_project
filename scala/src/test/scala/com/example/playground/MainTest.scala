package com.example.playground

import io.finch._
import org.scalatest._
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

class MainTest extends FunSuite with MockitoSugar{
  
  test("healthcheck") {
    assert(Main.healthcheck(Input.get("/")).awaitValue() == Some(Right("OK")))
  }

}
