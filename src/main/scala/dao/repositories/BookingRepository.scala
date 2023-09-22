package dao.repositories

import dao.entities._
import dao.entities.schema._
import io.getquill.context.ZioJdbc._
import utils.CommonUtils.dateConverters._
import zio._

object BookingRepository {

  type BookingRepo = Has[Service]

  import db.ctx._

  trait Service {

    def findBookingsByUser(userId: UserId): QIO[List[(Booking, MeetingRoom)]]

    def listMeetingRoomsByDepartment(departmentId: DepartmentId): QIO[List[MeetingRoom]]

    def listBookingByRoom(roomId: RoomId, from: Long, to: Long): QIO[List[(Booking, User)]]

    def createBooking(booking: Booking): QIO[Booking]

    def cancelBooking(bookingId: BookingId): QIO[Unit]

  }

  class BookingRepositoryImpl extends Service {

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

    override def listBookingByRoom(roomId: RoomId, from: Long, to: Long): QIO[List[(Booking, User)]] = run {
      for {
        booking <- bookingSchema.filter(b => b.startTime >= lift(from) && b.endTime <= lift(to) && b.roomId == lift(roomId.id))
        user <- userSchema.filter(_.id == booking.bookedByUser)
      } yield (booking, user)
    }

    override def createBooking(booking: Booking): QIO[Booking] =
      run(bookingSchema.insert(lift(booking))).as(booking)

    override def cancelBooking(bookingId: BookingId): QIO[Unit] = run {
      bookingSchema.filter(_.id == lift(bookingId.id)).delete
    }.unit
//      run(bookingSchema.filter(_.id == lift(bookingId.id))).map(_.headOption.map(_.canceled)).upd

  }

  val live: ULayer[BookingRepo] = ZLayer.succeed(new BookingRepositoryImpl)

}
