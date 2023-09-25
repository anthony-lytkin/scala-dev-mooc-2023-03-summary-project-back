package dto.requests

import dto.RequestDTO
import io.circe.{Decoder, Encoder, Json}
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

case class UserAuthDTO(login: String) extends RequestDTO

object UserAuthDTO {

  implicit val decoder: RootJsonFormat[UserAuthDTO] = jsonFormat1(UserAuthDTO.apply)

}
