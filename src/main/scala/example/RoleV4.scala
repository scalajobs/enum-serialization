package example

import io.circe.Codec
import io.circe.derivation.Configuration

// derived codec with discriminator
enum RoleV4 {
  case Reader(subscription: SubscriptionV1)
  case Editor(profileBio: String, favoriteFont: String)
  case Admin
}

object RoleV4 {
  given Configuration = Configuration.default
    .withDiscriminator("type")
    .withTransformConstructorNames(_.toUpperCase)

  given Codec[RoleV4] = Codec.AsObject.derivedConfigured
}
