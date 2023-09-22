package dto.responses

import dao.entities.MeetingRoom
import dto.ResponseDTO
import io.circe.Encoder
import io.circe.derivation.deriveEncoder

case class MeetingRoomDTO(id: String, name: String, floor: String, personNumber: Int) extends ResponseDTO

object MeetingRoomDTO {

  implicit val encoder: Encoder[MeetingRoomDTO] = deriveEncoder

  def apply(room: MeetingRoom): MeetingRoomDTO = MeetingRoomDTO(room.id, room.name, room.floor, room.personNumber)

}