package example

import cats.implicits.*
import example.RoleV3.*
import example.SubscriptionV1.*
import io.circe.parser.decode
import io.circe.syntax.*

class RoleV3Spec extends munit.FunSuite {

  test("encoder") {
    assertEquals(Reader(Free).asJson.noSpaces, """{"type":"READER","subscription":"FREE"}""")
    assertEquals(Editor("John is the winner of ...", "Comic Sans").asJson.noSpaces, """{"type":"EDITOR","profileBio":"John is the winner of ...","favoriteFont":"Comic Sans"}""")
    assertEquals(Admin.asJson.noSpaces, """{"type":"ADMIN"}""")
  }

  test("decoder") {
    val result1 = decode[RoleV3]("""{"type":"READER","subscription":"FREE"}""")
    val result2 = decode[RoleV3]("""{"type":"EDITOR","profileBio":"foo","favoriteFont":"Comic Sans"}""")
    val result3 = decode[RoleV3]("""{"type":"ADMIN"}""")
    val result4 = decode[RoleV3]("""{"type":"READER","subscription":"GENESIS"}""")
    val result5 = decode[RoleV3]("""{"type":"TESTER","subscription":"GENESIS"}""")

    assertEquals(result1, Right(Reader(Free)))
    assertEquals(result2, Right(Editor("foo","Comic Sans")))
    assertEquals(result3, Right(Admin))
    assertEquals(result4.leftMap(_.toString), Left("""DecodingFailure at .subscription: GENESIS is not a valid Subscription"""))
    assertEquals(result5.leftMap(_.toString), Left("""DecodingFailure at .type: invalid role TESTER"""))
  }

}
