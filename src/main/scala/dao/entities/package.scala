package dao

package object entities {

  case class UserId(id: String) extends AnyVal
  case class DepartmentId(id: String) extends AnyVal
  case class RoomId(id: String) extends AnyVal
  case class BookingId(id: String) extends AnyVal
  case class UserLogin(login: String) extends AnyVal

  private[dao] object schema {

    import db.ctx.{quote, querySchema}
    import io.getquill.{EntityQuery, Quoted}

    val userAccountSchema: Quoted[EntityQuery[UserEmail]]   = quote(querySchema[UserEmail](""""user_account""""))
    val userSchema: Quoted[EntityQuery[User]]               = quote(querySchema[User](""""user""""))
    val departmentSchema: Quoted[EntityQuery[Department]]   = quote(querySchema[Department](""""department""""))
    val bookingSchema: Quoted[EntityQuery[Booking]]         = quote(querySchema[Booking](""""booking""""))
    val meetingRoomSchema: Quoted[EntityQuery[MeetingRoom]] = quote(querySchema[MeetingRoom](""""meeting_room""""))

    val departmentToMeetingRoomSchema: Quoted[EntityQuery[DepartmentToMeetingRoom]] = quote(querySchema[DepartmentToMeetingRoom](""""department_to_meeting_room""""))


  }

}
