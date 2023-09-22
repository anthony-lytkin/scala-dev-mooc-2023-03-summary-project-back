package dto.requests

import dto.RequestDTO
import io.circe.Decoder
import io.circe.derivation.deriveDecoder

case class GetUserAuthRequest(login: String) extends RequestDTO

object GetUserAuthRequest {

  implicit val decoder: Decoder[GetUserAuthRequest] = deriveDecoder

}
