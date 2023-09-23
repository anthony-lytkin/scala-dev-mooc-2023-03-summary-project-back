package dto.responses

import dao.entities.{Booking, MeetingRoom}
import dto.ResponseDTO
import io.circe.Encoder
import io.circe.derivation.deriveEncoder
import utils.CommonUtils.dateConvertersImplicit._

import java.time.LocalDateTime

case class UserBookingDTO(id: String, roomName: String, floor: String, startTime: Long, endTime: Long) extends ResponseDTO

object UserBookingDTO {

  implicit val encoder: Encoder[UserBookingDTO] = deriveEncoder

  def apply(booking: (Booking, MeetingRoom)): UserBookingDTO = UserBookingDTO(
    booking._1.id,
    booking._2.name,
    booking._2.floor,
    booking._1.startTime,
    booking._1.endTime
  )

}
