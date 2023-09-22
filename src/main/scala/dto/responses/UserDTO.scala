package dto.responses

import dao.entities.{Department, User, UserEmail}
import dto.ResponseDTO
import io.circe.Encoder
import io.circe.derivation.deriveEncoder

case class UserDTO(id: String,
                   firstName: String,
                   lastName: String,
                   middleName: Option[String],
                   email: String,
                   departmentId: String,
                   departmentName: String) extends ResponseDTO

object UserDTO {

  implicit val encoder: Encoder[UserDTO] = deriveEncoder

  def apply(user: User, email: UserEmail, department: Department): UserDTO = UserDTO(
    user.id,
    user.firstName,
    user.lastName,
    user.middleName,
    email.email,
    department.id,
    department.name
  )

}
