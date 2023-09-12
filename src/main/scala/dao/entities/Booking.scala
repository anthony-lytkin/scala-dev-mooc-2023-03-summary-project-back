package dao.entities

import java.time.LocalDateTime

case class Booking(id: String, userId: String, roomId: String, topic: Option[String], startTime: LocalDateTime, endTime: LocalDateTime) {

  def bookingId: BookingId = BookingId(id)

}
