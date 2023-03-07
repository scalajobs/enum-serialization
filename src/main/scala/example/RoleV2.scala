package example

import cats.implicits.*
import io.circe.DecodingFailure.Reason.CustomReason
import io.circe.syntax.*
import io.circe.*

// manual codec with strict admin decoder
enum RoleV2 {
  case Reader(subscription: SubscriptionV1)
  case Editor(profileBio: String, favoriteFont: String)
  case Admin
}

object RoleV2 {
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

  given Encoder[RoleV2] =
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
        _ <- if(obj.isEmpty) Right(Admin)
             else Left(DecodingFailure(CustomReason(s"$obj is not a valid Admin"), cursor))
      } yield Admin
    )

  given Decoder[RoleV2] =
    readerDecoder.widen[RoleV2]
      .or(editorDecoder.widen[RoleV2])
      .or(adminDecoder.widen[RoleV2])

}
