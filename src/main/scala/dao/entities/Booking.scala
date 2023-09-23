package dao.entities

import java.time.LocalDateTime

case class Booking(id: String, bookedByUser: String, roomId: String, startTime: LocalDateTime, endTime: LocalDateTime) {

  def bookingId: BookingId = BookingId(id)

  endTime

}
