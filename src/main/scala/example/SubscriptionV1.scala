package example

import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import io.circe.DecodingFailure.Reason.CustomReason
import io.circe.syntax.*

enum SubscriptionV1(val id: String) {
  case Free    extends SubscriptionV1("FREE")
  case Premium extends SubscriptionV1("PREMIUM")
}

object SubscriptionV1 {
  given Encoder[SubscriptionV1] =
    Encoder.instance(subscription => subscription.id.asJson)

  given Decoder[SubscriptionV1] =
    Decoder.instance(cursor =>
      for {
        str          <- cursor.as[String]
        subscription <- str match
          case "FREE"    => Right(Free)
          case "PREMIUM" => Right(Premium)
          case other     => Left(DecodingFailure(CustomReason(s"$other is not a valid Subscription"), cursor))
      } yield subscription
    )
}