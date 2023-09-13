import zio._
import zio.config.ReadError.SourceError
import zio.config.magnolia.DeriveConfigDescriptor.descriptor
import zio.config._
import zio.config.typesafe.TypesafeConfig

package object configurations {

//  trait AppConfig

  case class AppConfig(jdbc: JDBCConfig, liquibase: LiquibaseConfig)

  case class JDBCConfig(host: String, user: String, password: String, driver: String)
  case class LiquibaseConfig(changeLogPath: String) extends AnyVal

  type JDBCConfiguration = Has[JDBCConfig]
  type LiquibaseConfiguration = Has[LiquibaseConfig]

  object applicationConf {


    val jdbc: ConfigDescriptor[JDBCConfig] = descriptor[JDBCConfig]

    val liquibase: ConfigDescriptor[LiquibaseConfig] = descriptor[LiquibaseConfig]

  }

  import applicationConf._

  object ConfigurationLive {

    val liveJdbcConfig: Layer[ReadError[String], JDBCConfiguration] = TypesafeConfig.fromDefaultLoader(applicationConf.jdbc)

    val liveLiquibaseConfig: Layer[ReadError[String], LiquibaseConfiguration] = TypesafeConfig.fromDefaultLoader(applicationConf.liquibase)

  }

  def loadJDBCConfig: Task[JDBCConfiguration] =
    ZIO.accessM[JDBCConfiguration](ZIO.effect).provideLayer(ConfigurationLive.liveJdbcConfig)

  def loadLiquibseConfig: Task[LiquibaseConfiguration] =
    ZIO.accessM[LiquibaseConfiguration](ZIO.effect).provideLayer(ConfigurationLive.liveLiquibaseConfig)


}