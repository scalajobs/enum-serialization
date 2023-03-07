package example

import cats.implicits.*
import example.RoleV4.*
import example.SubscriptionV1.*
import io.circe.parser.decode
import io.circe.syntax.*

class RoleV4Spec extends munit.FunSuite {

  test("encoder") {
    val role1: RoleV4 = Reader(Free)
    val role2: RoleV4 = Editor("John is the winner of ...", "Comic Sans")
    val role3: RoleV4 = Admin

    assertEquals(role1.asJson.noSpaces, """{"subscription":"FREE","type":"READER"}""")
    assertEquals(role2.asJson.noSpaces, """{"profileBio":"John is the winner of ...","favoriteFont":"Comic Sans","type":"EDITOR"}""")
    assertEquals(role3.asJson.noSpaces, """{"type":"ADMIN"}""")
  }

  test("decoder") {
    val result1 = decode[RoleV4]("""{"type":"READER","subscription":"FREE"}""")
    val result2 = decode[RoleV4]("""{"type":"EDITOR","profileBio":"foo","favoriteFont":"Comic Sans"}""")
    val result3 = decode[RoleV4]("""{"type":"ADMIN"}""")
    val result4 = decode[RoleV4]("""{"type":"READER","subscription":"GENESIS"}""")
    val result5 = decode[RoleV4]("""{"type":"TESTER","subscription":"GENESIS"}""")

    assertEquals(result1, Right(Reader(Free)))
    assertEquals(result2, Right(Editor("foo","Comic Sans")))
    assertEquals(result3, Right(Admin))
    assertEquals(result4.leftMap(_.toString), Left("""DecodingFailure at .subscription: GENESIS is not a valid Subscription"""))
    assertEquals(result5.leftMap(_.toString), Left("""DecodingFailure at : type RoleV4 has no class/object/case named 'TESTER'."""))
  }

}
