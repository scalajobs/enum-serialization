package example

import cats.implicits.*
import example.RoleV2.*
import example.SubscriptionV1.*
import io.circe.parser.decode
import io.circe.syntax.*

class RoleV2Spec extends munit.FunSuite {

  test("encoder") {
    assertEquals(Reader(Free).asJson.noSpaces, """{"subscription":"FREE"}""")
    assertEquals(Editor("John is the winner of ...", "Comic Sans").asJson.noSpaces, """{"profileBio":"John is the winner of ...","favoriteFont":"Comic Sans"}""")
    assertEquals(Admin.asJson.noSpaces, """{}""")
  }

  test("decoder") {
    val result1 = decode[RoleV2]("""{"subscription":"FREE"}""")
    val result2 = decode[RoleV2]("""{"profileBio":"foo","favoriteFont":"Comic Sans"}""")
    val result3 = decode[RoleV2]("""{}""")
    val result4 = decode[RoleV2]("""{"subscription":"GENESIS"}""")

    assertEquals(result1, Right(Reader(Free)))
    assertEquals(result2, Right(Editor("foo","Comic Sans")))
    assertEquals(result3, Right(Admin))
    assertEquals(result4.leftMap(_.toString), Left("""DecodingFailure at : object[subscription -> "GENESIS"] is not a valid Admin"""))
  }

  test("Decoder failure for each branch") {
    val result1 = decode[Reader]("""{"subscription":"GENESIS"}""")
    val result2 = decode[Editor]("""{"subscription":"GENESIS"}""")
    val result3 = decode[Admin.type]("""{"subscription":"GENESIS"}""")

    assertEquals(result1.leftMap(_.toString), Left("""DecodingFailure at .subscription: GENESIS is not a valid Subscription"""))
    assertEquals(result2.leftMap(_.toString), Left("""DecodingFailure at .profileBio: Missing required field"""))
    assertEquals(result3.leftMap(_.toString), Left("""DecodingFailure at : object[subscription -> "GENESIS"] is not a valid Admin"""))
  }

}
