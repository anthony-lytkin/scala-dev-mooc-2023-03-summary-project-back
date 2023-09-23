package dao.entities

case class User(id: String, lastName: String, firstName: String, middleName: Option[String], departmentId: String) {

  def userId: UserId = UserId(id)

}
