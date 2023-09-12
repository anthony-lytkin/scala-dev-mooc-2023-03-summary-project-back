package dao.entities

import java.time.LocalDateTime

case class MeetingRoom(id: String, name: String, floor: String, personNumber: Int, departmentId: String) {

  def roomId: RoomId = RoomId(id)

}
