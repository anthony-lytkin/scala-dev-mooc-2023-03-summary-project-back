import configurations._
import configurations.common.{JDBCConfig, LiquibaseConfig}
import zio.{Layer, Scope, ZEnvironment, ZIO, ZLayer}
import zio.config.ReadError

object Application {

  val configEnv: Layer[ReadError[String], AppConfig] = jdbcConfigLayer

  def run: ZIO[Scope, Exception, String] = ???


}
