package example

import example.RoleV1.*
import example.SubscriptionV1.*
import io.circe.syntax.*
import io.circe.parser.decode

class RoleV1Spec extends munit.FunSuite {

  test("encoder") {
    assertEquals(Reader(Free).asJson.noSpaces, """{"subscription":"FREE"}""")
    assertEquals(Editor("John is the winner of ...", "Comic Sans").asJson.noSpaces, """{"profileBio":"John is the winner of ...","favoriteFont":"Comic Sans"}""")
    assertEquals(Admin.asJson.noSpaces, """{}""")
  }

  test("decoder") {
    val result1 = decode[RoleV1]("""{"subscription":"FREE"}""")
    val result2 = decode[RoleV1]("""{"profileBio":"foo","favoriteFont":"Comic Sans"}""")
    val result3 = decode[RoleV1]("""{}""")
    val result4 = decode[RoleV1]("""{"subscription":"GENESIS"}""")

    assertEquals(result1, Right(Reader(Free)))
    assertEquals(result2, Right(Editor("foo","Comic Sans")))
    assertEquals(result3, Right(Admin))
    assertEquals(result4, Right(Admin)) // It shouldn't !!
  }

}
