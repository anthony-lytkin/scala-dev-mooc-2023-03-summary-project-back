package dto.requests

import dto.RequestDTO
import spray.json._
import spray.json.DefaultJsonProtocol._
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor}
import io.circe.derivation.{deriveDecoder, deriveEncoder}

case class CreateBookingDTO(roomId: String, startTime: Long, endTime: Long) extends RequestDTO

object CreateBookingDTO {

  implicit val decoder: RootJsonFormat[CreateBookingDTO] = jsonFormat3(CreateBookingDTO.apply)

}
