package vo

import dao.entities.{Booking, RoomId, User}
import dto.requests.CreateBookingDTO
import io.circe.{Encoder, Json}
import utils.CommonUtils.dateConvertersImplicit._

import java.time.LocalDateTime

case class BookingVO(roomId: String, bookingId: Option[String], userId: Option[String], bookedByUser: Option[String], startTime: LocalDateTime, endTime: LocalDateTime) {

  def typedRoomId: RoomId = RoomId(roomId)

}

object BookingVO {

  implicit val encoder: Encoder[BookingVO] = (a: BookingVO) => Json.obj(
    ("bookingId", a.bookingId.fold(Json.Null)(Json.fromString)),
    ("userId", a.userId.fold(Json.Null)(Json.fromString)),
    ("bookedByUser", a.bookedByUser.fold(Json.Null)(Json.fromString)),
    ("startTime", Json.fromLong(a.startTime)),
    ("endTime", Json.fromLong(a.endTime))
  )

  def apply(booking: Booking, user: User): BookingVO = BookingVO(
    booking.roomId,
    Some(booking.id),
    Some(s"${user.lastName} ${user.firstName} ${user.middleName.getOrElse("")}"),
    Some(user.id),
    booking.startTime,
    booking.endTime
  )

  def apply(booking: Booking): BookingVO = apply(booking.roomId, Some(booking.id), None, None, booking.startTime, booking.endTime)

  def apply(roomId: String, startTime: Long, endTime: Long): BookingVO = apply(roomId, None, None, None, startTime, endTime)

  def apply(dto: CreateBookingDTO): BookingVO = apply(dto.roomId, None, None, None, dto.startTime, dto.endTime)

}