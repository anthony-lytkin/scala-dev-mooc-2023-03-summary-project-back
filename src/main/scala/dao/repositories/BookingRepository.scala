package dao.repositories

import dao.entities._
import dao.entities.schema._
import io.getquill.context.ZioJdbc._
import vo.BookingVO
import zio._

object BookingRepository {

  type BookingRepo = Has[Service]

  import db.ctx._

  trait Service {

    def findBooking(bookingId: BookingId): QIO[Option[(Booking, MeetingRoom)]]

    def findMeetingRoom(roomId: RoomId): QIO[Option[MeetingRoom]]

    def findBookingByRoomIdAndBookingTime(booking: BookingVO): QIO[Option[Booking]]

    def findBookingsByUser(userId: UserId): QIO[List[(Booking, MeetingRoom)]]

    def listMeetingRoomsByDepartment(departmentId: DepartmentId): QIO[List[MeetingRoom]]

    def listBookingByRoom(booking: BookingVO): QIO[List[(Booking, User)]]

    def insertBooking(booking: Booking): QIO[Booking]

    def deleteBooking(bookingId: BookingId): QIO[Unit]

  }

  class BookingRepositoryImpl extends Service {

    import db.ctx._

    override def findBooking(bookingId: BookingId): QIO[Option[(Booking, MeetingRoom)]] = run {
      for {
        booking <- bookingSchema.filter(_.id == lift(bookingId.id))
        room <- meetingRoomSchema.join(_.id == booking.roomId)
      } yield (booking, room)
    }.map(_.headOption)

    override def findMeetingRoom(roomId: RoomId): QIO[Option[MeetingRoom]] =
      run(meetingRoomSchema.filter(_.id == lift(roomId.id))).map(_.headOption)

    override def findBookingByRoomIdAndBookingTime(booking: BookingVO): QIO[Option[Booking]] = run {
      bookingSchema.filter(b => b.roomId == lift(booking.roomId) && b.startTime == lift(booking.startTime) && b.endTime == lift(booking.endTime))
    }.map(_.headOption)

    override def findBookingsByUser(userId: UserId): QIO[List[(Booking, MeetingRoom)]] = run {
      for {
        room <- meetingRoomSchema
        booking <- bookingSchema.join(_.roomId == room.id).filter(_.bookedByUser == lift(userId.id))
        _ <- userSchema.leftJoin(_.id == booking.bookedByUser)
      } yield (booking, room)
    }

    override def listMeetingRoomsByDepartment(depId: DepartmentId): QIO[List[MeetingRoom]] = run {
      for {
        dep <- departmentSchema.filter(_.id == lift(depId.id))
        dep2Room <- departmentToMeetingRoomSchema.join(_.departmentId == dep.id)
        rooms <- meetingRoomSchema.join(_.id == dep2Room.meetingRoomId)
      } yield rooms
    }

    override def listBookingByRoom(booking: BookingVO): QIO[List[(Booking, User)]] = run {
      for {
        booking <- bookingSchema.filter(_.roomId == lift(booking.roomId)) // todo filter by date
        user <- userSchema.filter(_.id == booking.bookedByUser)
      } yield (booking, user)
    }

    override def insertBooking(booking: Booking): QIO[Booking] =
      run(bookingSchema.insert(lift(booking))).as(booking)

    override def deleteBooking(bookingId: BookingId): QIO[Unit] =
      run(bookingSchema.filter(_.id == lift(bookingId.id)).delete).unit
//      run(bookingSchema.filter(_.id == lift(bookingId.id))).map(_.headOption.map(_.canceled)).upd
  }

  val live: ULayer[BookingRepo] = ZLayer.succeed(new BookingRepositoryImpl)

}
