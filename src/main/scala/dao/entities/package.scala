package dao

package object entities {

  case class UserId(id: String) extends AnyVal
  case class DepartmentId(id: String) extends AnyVal
  case class RoomId(id: String) extends AnyVal
  case class BookingId(id: String) extends AnyVal

}
