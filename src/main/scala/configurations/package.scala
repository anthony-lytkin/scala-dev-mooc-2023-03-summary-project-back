import zio._
import zio.config._
import zio.config.magnolia.DeriveConfigDescriptor.descriptor
import zio.config.typesafe.TypesafeConfig

package object configurations {

//  trait AppConfig

  case class ApplicationConfig (db: JDBCConfig, liquibase: LiquibaseConfig, server: ServerConfig)

  case class JDBCConfig(driver: String, url: String, user: String, password: String, schema: String)
  case class LiquibaseConfig(changeLog: String) extends AnyVal
  case class ServerConfig(port: Int) extends AnyVal


//  type JDBCConfiguration = Has[JDBCConfig]
//  type LiquibaseConfiguration = Has[LiquibaseConfig]
//  type ServerConfiguration = Has[ServerConfig]
  type ApplicationConfiguration = Has[ApplicationConfig]

  private object applicationConf {


//    val jdbc: ConfigDescriptor[JDBCConfig] = descriptor[JDBCConfig]
//
//    val liquibase: ConfigDescriptor[LiquibaseConfig] = descriptor[LiquibaseConfig]
//
//    val server: ConfigDescriptor[ServerConfig] = descriptor[ServerConfig]

    val fullConfig: ConfigDescriptor[ApplicationConfig] = descriptor[ApplicationConfig]

  }

  object ConfigurationLive {

//    val liveJdbcConfig: Layer[ReadError[String], JDBCConfiguration] = TypesafeConfig.fromDefaultLoader(applicationConf.jdbc)
//
//    val liveLiquibaseConfig: Layer[ReadError[String], LiquibaseConfiguration] = TypesafeConfig.fromDefaultLoader(applicationConf.liquibase)
//
//    val liveServerConfig: Layer[ReadError[String], ServerConfiguration] = TypesafeConfig.fromDefaultLoader(applicationConf.server)

    val liveApplicationConfig: Layer[ReadError[String], ApplicationConfiguration] = TypesafeConfig.fromDefaultLoader(applicationConf.fullConfig)

  }

//  def loadJDBCConfig: Task[JDBCConfiguration] =
//    ZIO.accessM[JDBCConfiguration](cfg => ZIO.effect(cfg)).provideLayer(ConfigurationLive.liveJdbcConfig)
//
//  def loadLiquibseConfig: Task[LiquibaseConfiguration] =
//    ZIO.accessM[LiquibaseConfiguration](cfg => ZIO.effect(cfg)).provideLayer(ConfigurationLive.liveLiquibaseConfig)
//
//  def loadServerConfig: Task[ServerConfiguration] =
//    ZIO.accessM[ServerConfiguration](cfg => ZIO.effect(cfg)).provideLayer(ConfigurationLive.liveServerConfig)

  val getConfig: ZIO[Any, Throwable, ApplicationConfiguration] =
    ZIO.accessM[ApplicationConfiguration](cfg => ZIO.effect(cfg)).provideLayer(ConfigurationLive.liveApplicationConfig)


}