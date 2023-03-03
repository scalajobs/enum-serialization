package example

import io.circe.{DecodingFailure, Json}
import io.circe.syntax.*
import SubscriptionV1.*

class SubscriptionV1Spec extends munit.FunSuite {

  test("encoder") {
    assertEquals(Free.asJson, Json.fromString("FREE"))
    assertEquals(Premium.asJson, Json.fromString("PREMIUM"))
  }

  test("decoder") {
    val result1 = Json.fromInt(5).as[SubscriptionV1]
    val result2 = Json.fromString("SPECIAL").as[SubscriptionV1]
    val result3 = Json.fromString("FREE").as[SubscriptionV1]

    assertEquals(result1.isLeft, true)
    assertEquals(result2.isLeft, true)
    assertEquals(result3, Right(Free))
  }

}
