import zio._
import zio.config._
import zio.config.magnolia.DeriveConfigDescriptor.descriptor
import zio.config.typesafe.TypesafeConfig

package object configurations {

  case class ApplicationConfig (db: JDBCConfig, liquibase: LiquibaseConfig, server: ServerConfig)

  case class JDBCConfig(driver: String, url: String, user: String, password: String, schema: String)
  case class LiquibaseConfig(changeLog: String) extends AnyVal
  case class ServerConfig(port: Int) extends AnyVal

  type ApplicationConfiguration = Has[ApplicationConfig]

  private object applicationConf {

    val jdbc: ConfigDescriptor[JDBCConfig] = descriptor[JDBCConfig]
    val liquibase: ConfigDescriptor[LiquibaseConfig] = descriptor[LiquibaseConfig]
    val server: ConfigDescriptor[ServerConfig] = descriptor[ServerConfig]
    val fullConfig: ConfigDescriptor[ApplicationConfig] = descriptor[ApplicationConfig]

  }

  object ConfigurationLive {

    val liveApplicationConfig: Layer[ReadError[String], ApplicationConfiguration] = TypesafeConfig.fromDefaultLoader(applicationConf.fullConfig)

  }

  val getConfig: ZIO[Any, Throwable, ApplicationConfiguration] =
    ZIO.accessM[ApplicationConfiguration](cfg => ZIO.effect(cfg)).provideLayer(ConfigurationLive.liveApplicationConfig)


}