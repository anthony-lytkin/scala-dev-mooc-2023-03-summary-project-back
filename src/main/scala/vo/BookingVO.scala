package vo

import dao.entities.{Booking, RoomId, User}
import dto.requests.CreateBookingDTO
import io.circe.Encoder
import io.circe.derivation.deriveEncoder
import utils.CommonUtils.dateConvertersImplicit._

import java.time.LocalDateTime

case class BookingVO(roomId: String, bookedByUser: Option[String], startTime: LocalDateTime, endTime: LocalDateTime) {

  def typedRoomId: RoomId = RoomId(roomId)

}

object BookingVO {

  implicit val encoder: Encoder[BookingVO] = deriveEncoder

  def apply(booking: Booking, user: User): BookingVO = BookingVO(
    booking.roomId,
    Some(s"${user.lastName} ${user.firstName} ${user.middleName}"),
    booking.startTime,
    booking.endTime
  )

  def apply(booking: Booking): BookingVO = apply(booking.roomId, None, booking.startTime, booking.endTime)

  def apply(roomId: String, startTime: Long, endTime: Long): BookingVO = apply(roomId, None, startTime, endTime)

  def apply(dto: CreateBookingDTO): BookingVO = apply(dto.roomId, None, dto.startTime, dto.endTime)

}