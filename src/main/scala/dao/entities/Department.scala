package dao.entities

case class Department(id: String, name: String) {

  def departmentId: DepartmentId = DepartmentId(id)

}
