package example

import example.RoleV1
import example.SubscriptionV1.Premium
import io.circe.syntax.*
import io.circe.Json
import io.circe.syntax._

object EnumJsonExample extends App {

  println(RoleV1.Reader(Premium).asJson.spaces2)
  println(RoleV1.Editor("John is the winner of ...", "Comic Sans").asJson.spaces2)
  println(RoleV1.Admin.asJson.spaces2)


  val readerV1 = RoleV1.Reader(Premium)
  val readerV2 = RoleV2.Reader("john@scalajobs.com", Premium)
  val readerV3 = RoleV3.Reader("john@scalajobs.com", Premium)

  println(readerV1.asJson.spaces2)
  println(readerV2.asJson.spaces2)
  println(readerV3.asJson.spaces2)

  val roleJson = Json.obj(
    "type" -> "READER".asJson,
    "email" -> "bob@scalajobs.com".asJson,
    "subscription" -> "GENESIS".asJson,
  )

  println(roleJson.as[RoleV1])
  println(roleJson.as[RoleV2])
  println(roleJson.as[RoleV3])

}
