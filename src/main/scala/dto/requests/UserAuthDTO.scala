package dto.requests

import dto.RequestDTO
import io.circe.Decoder
import io.circe.derivation.deriveDecoder

case class UserAuthDTO(login: String) extends RequestDTO

object UserAuthDTO {

  implicit val decoder: Decoder[UserAuthDTO] = deriveDecoder

}
