package configurations.common

case class JDBCConfig(host: String, user: String, password: String, driver: String) {

  override def toString: String =
    s"""JDBC Configurations:
      |Host: $host
      |User: $user
      |""".stripMargin

}

object JDBCConfig {
  val default: JDBCConfig = JDBCConfig("", "", "", "")
}