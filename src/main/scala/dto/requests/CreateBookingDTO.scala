package dto.requests

import dto.RequestDTO
import io.circe.Decoder
import io.circe.derivation.deriveDecoder

case class CreateBookingDTO(roomId: String, startTime: Long, endTime: Long) extends RequestDTO

object CreateBookingDTO {

  implicit val decoder: Decoder[CreateBookingDTO] = deriveDecoder

}
