import com.zaxxer.hikari._
import io.getquill._
import io.getquill.context.ZioJdbc
import zio._

import javax.sql

package object db {

  type DataSource = Has[javax.sql.DataSource]
  type Hikari = Has[HikariDataSource]

  object ctx extends PostgresZioJdbcContext(NamingStrategy(Escape, Literal, SnakeCase))

  object hikari {

    def hikariDs: TaskManaged[HikariDataSource] = ZManaged.fromEffect(
      for {
        appConfig <- configurations.getConfig.map(_.get)
        hikariConfig <- ZIO.effect {
          val hc = new HikariConfig()
          hc.setDriverClassName(appConfig.db.driver)
          hc.setJdbcUrl(appConfig.db.url)
          hc.setUsername(appConfig.db.user)
          hc.setPassword(appConfig.db.password)
          hc
        }.orDie
        hikariDs <- ZIO.succeed(new HikariDataSource(hikariConfig))
      } yield hikariDs
    )

    val live: ZLayer[Any, Throwable, Hikari] = ZLayer.fromManaged(hikariDs)

  }

//  val live: ZLayer[Any, Throwable, DataSource] = ZioJdbc.DataSourceLayer.fromDataSource(hikari.hikariDS)

  val live: TaskLayer[DataSource] =
    ZLayer.fromManaged(hikari.hikariDs).flatMap(hikari => ZioJdbc.DataSourceLayer.fromDataSource(hikari.get))

}
