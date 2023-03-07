package example

import cats.implicits.*
import io.circe.DecodingFailure.Reason.CustomReason
import io.circe.syntax.*
import io.circe.*

// manual codec with discriminator
enum RoleV3 {
  case Reader(subscription: SubscriptionV1)
  case Editor(profileBio: String, favoriteFont: String)
  case Admin
}

object RoleV3 {
  given readerEncoder: Encoder[Reader] =
    Encoder.instance { reader =>
      Json.obj(
        "type" -> "READER".asJson,
        "subscription" -> reader.subscription.asJson
      )
    }

  given editorEncoder: Encoder[Editor] =
    Encoder.instance { editor =>
      Json.obj(
        "type" -> "EDITOR".asJson,
        "profileBio"     -> editor.profileBio.asJson,
        "favoriteFont"   -> editor.favoriteFont.asJson,
      )
    }

  given adminEncoder: Encoder[Admin.type] =
    Encoder.instance { admin =>
      Json.obj("type" -> "ADMIN".asJson)
    }

  given Encoder[RoleV3] =
    Encoder.instance {
      case x: Reader => readerEncoder(x)
      case x: Editor => editorEncoder(x)
      case Admin     => adminEncoder(Admin)
    }

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

  given Decoder[RoleV3] =
    Decoder.instance(cursor =>
      for {
        discriminator <- cursor.downField("type").as[String]
        role <- discriminator match
          case "READER" => readerDecoder(cursor)
          case "EDITOR" => editorDecoder(cursor)
          case "ADMIN"  => adminDecoder(cursor)
          case other    => Left(DecodingFailure(CustomReason(s"invalid role $other"), cursor.downField("type")))
      } yield role
    )

}
