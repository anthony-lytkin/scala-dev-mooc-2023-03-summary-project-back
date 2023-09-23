package dto.responses

import dao.entities.MeetingRoom
import dto.ResponseDTO
import io.circe.Encoder
import io.circe.derivation.deriveEncoder
import vo.BookingVO

case class BookingRoomDetailsDTO(id: String, name: String, floor: String, personMumber: Int, bookings: List[BookingVO]) extends ResponseDTO

object BookingRoomDetailsDTO {

  implicit val encoder: Encoder[BookingRoomDetailsDTO] = deriveEncoder

  def apply(room: MeetingRoom, bookings: List[BookingVO]): BookingRoomDetailsDTO = BookingRoomDetailsDTO(
    room.id,
    room.name,
    room.floor,
    room.personNumber,
    bookings
  )

}
