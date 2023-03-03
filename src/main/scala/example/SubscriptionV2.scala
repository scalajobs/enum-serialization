package example

import io.circe.syntax.*
import io.circe.{Decoder, Encoder}

enum SubscriptionV2(val id: String) {
  case Free    extends SubscriptionV2("FREE")
  case Premium extends SubscriptionV2("PREMIUM")
}

object SubscriptionV2 {
  def parseId(id: String): Either[String, SubscriptionV2] =
    values
      .find(_.id == id)
      .toRight(s"$id is not a valid Subscription")

  given Encoder[SubscriptionV2] =
    Encoder[String].contramap(_.id)

  given Decoder[SubscriptionV2] =
    Decoder[String].emap(parseId)
}