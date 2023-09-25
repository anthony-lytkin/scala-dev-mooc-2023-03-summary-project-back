package dto.requests

import dto.RequestDTO
import io.circe.{Decoder, Encoder, Json}
import io.circe.derivation.{deriveDecoder, deriveEncoder}

case class UserAuthDTO(login: String) extends RequestDTO

object UserAuthDTO {

  implicit val encoder: Encoder[UserAuthDTO] = deriveEncoder
  implicit val decoder: Decoder[UserAuthDTO] = deriveDecoder

}
