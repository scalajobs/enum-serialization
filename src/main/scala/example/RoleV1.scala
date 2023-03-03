package example

import cats.implicits.*
import io.circe.{Decoder, Encoder, Json, JsonObject}
import io.circe.syntax.*

enum RoleV1 {
  case Reader(subscription: SubscriptionV1)
  case Editor(profileBio: String, favoriteFont: String)
  case Admin
}

object RoleV1 {
  given readerEncoder: Encoder[Reader] =
    Encoder.instance { reader =>
      Json.obj("subscription" -> reader.subscription.asJson)
    }

  given editorEncoder: Encoder[Editor] =
    Encoder.instance { editor =>
      Json.obj(
        "profileBio"     -> editor.profileBio.asJson,
        "favoriteFont"   -> editor.favoriteFont.asJson,
      )
    }

  given adminEncoder: Encoder[Admin.type] =
    Encoder.instance { admin => Json.obj() }

  given Encoder[RoleV1] =
    Encoder.instance {
      case x: Reader => readerEncoder(x)
      case x: Editor => editorEncoder(x)
      case Admin     => adminEncoder(Admin)
    }

//  given Encoder[RoleV1] =
//    Encoder.instance {
//      case Reader(subscription) =>
//        Json.obj("subscription" -> subscription.asJson)
//      case Editor(profileBio, favoriteFont) =>
//        Json.obj(
//          "profileBio" -> profileBio.asJson,
//          "favoriteFont"   -> favoriteFont.asJson,
//        )
//      case Admin =>
//        Json.obj()
//    }

  given readerDecoder: Decoder[Reader] =
    Decoder.instance(cursor =>
      for {
        subscription <- cursor.downField("subscription").as[SubscriptionV1]
      } yield Reader(subscription)
    )

  given editorDecoder: Decoder[Editor] =
    Decoder.instance(cursor =>
      for {
        profileBio   <- cursor.downField("profileBio").as[String]
        favoriteFont <- cursor.downField("favoriteFont").as[String]
      } yield Editor(profileBio, favoriteFont)
    )

  given adminDecoder: Decoder[Admin.type] =
    Decoder.instance(cursor =>
      for {
        obj <- cursor.as[JsonObject]
      } yield Admin
    )

  given Decoder[RoleV1] =
    readerDecoder.widen[RoleV1]
      .or(editorDecoder.widen[RoleV1])
      .or(adminDecoder.widen[RoleV1])

}
