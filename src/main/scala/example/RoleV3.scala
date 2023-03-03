package example

import io.circe.Codec
import io.circe.derivation.Configuration

enum RoleV3 {
  case Reader(email: String, subscription: SubscriptionV1)
  case Editor(email: String, favoriteFont: String)
  case Admin(email: String)
}

object RoleV3 {
  given Configuration = Configuration.default
    .withDiscriminator("type")
    .withTransformConstructorNames(_.toUpperCase)

  given Codec[RoleV3] = Codec.AsObject.derivedConfigured
}
