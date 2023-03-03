package example

import example.SubscriptionV2.*
import io.circe.syntax.*
import io.circe.{DecodingFailure, Json}

class SubscriptionV2Spec extends munit.FunSuite {

  test("encoder") {
    assertEquals(Free.asJson, Json.fromString("FREE"))
    assertEquals(Premium.asJson, Json.fromString("PREMIUM"))
  }

  test("decoder") {
    val result1 = Json.fromInt(5).as[SubscriptionV2]
    val result2 = Json.fromString("SPECIAL").as[SubscriptionV2]
    val result3 = Json.fromString("FREE").as[SubscriptionV2]

    assertEquals(result1.isLeft, true)
    assertEquals(result2.isLeft, true)
    assertEquals(result3, Right(Free))
  }

}
