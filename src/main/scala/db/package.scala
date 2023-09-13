import com.zaxxer.hikari.HikariDataSource
import io.getquill._
import io.getquill.util.LoadConfig
import liquibase.Liquibase
import zio.{Config, ULayer, ZIO, ZLayer}

import javax.sql.DataSource

package object db {

  object ctx extends PostgresZioJdbcContext(NamingStrategy(Escape, Literal))

  def hikariDS: HikariDataSource = JdbcContextConfig(LoadConfig("db")).dataSource

  val zioDS: ULayer[HikariDataSource] = ZLayer.succeed(hikariDS)

}
