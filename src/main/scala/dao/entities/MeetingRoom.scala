package dao.entities

case class MeetingRoom(id: String, name: String, floor: String, personNumber: Int) {

  def roomId: RoomId = RoomId(id)

}
