package dto.requests

import dto.RequestDTO
import io.circe.{Decoder, Encoder}
import io.circe.derivation.{deriveDecoder, deriveEncoder}

case class CreateBookingDTO(roomId: String, startTime: Long, endTime: Long) extends RequestDTO

object CreateBookingDTO {

  implicit val encoder: Encoder[CreateBookingDTO] = deriveEncoder
  implicit val decoder: Decoder[CreateBookingDTO] = deriveDecoder

}
