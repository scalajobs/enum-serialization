package example

import cats.implicits.*
import io.circe.syntax.*
import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import io.circe.DecodingFailure.Reason.CustomReason

enum RoleV2 {
  def email: String

  case Reader(email: String, subscription: SubscriptionV1)
  case Editor(email: String, favoriteFont: String)
  case Admin(email: String)
}

object RoleV2 {
  given Encoder[RoleV2] =
    Encoder.instance {
      case Reader(email, subscription) =>
        Json.obj(
          "type" -> "READER".asJson,
          "email" -> email.asJson,
          "subscription"   -> subscription.asJson,
        )
      case Editor(email, favoriteFont) =>
        Json.obj(
          "type" -> "EDITOR".asJson,
          "email" -> email.asJson,
          "favoriteFont"   -> favoriteFont.asJson,
        )
      case Admin(email) =>
        Json.obj(
          "type" -> "ADMIN".asJson,
          "email" -> email.asJson,
        )
    }

  given readerDecoder: Decoder[Reader] =
    Decoder.instance(cursor =>
      for {
        email        <- cursor.downField("email").as[String]
        subscription <- cursor.downField("subscription").as[SubscriptionV1]
      } yield Reader(email, subscription)
    )

  given editorDecoder: Decoder[Editor] =
    Decoder.instance(cursor =>
      for {
        email        <- cursor.downField("email").as[String]
        favoriteFont <- cursor.downField("favoriteFont").as[String]
      } yield Editor(email, favoriteFont)
    )
    
  given adminDecoder: Decoder[Admin] =
    Decoder.instance(cursor =>
      for {
        email <- cursor.downField("email").as[String]
      } yield Admin(email)
    )

  given Decoder[RoleV2] =
    Decoder.instance(cursor =>
      for {
        discriminator <- cursor.downField("type").as[String]
        role <- discriminator match
          case "READER" => readerDecoder(cursor)
          case "EDITOR" => editorDecoder(cursor)
          case "ADMIN"  => adminDecoder(cursor)
          case other    => Left(DecodingFailure(CustomReason(s"invalid role type $other"), cursor.downField("type")))
      } yield role
    )

}
