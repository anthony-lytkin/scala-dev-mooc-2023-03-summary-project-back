import configurations.common._
import zio._
import zio.config.magnolia.deriveConfig

package object configurations {

  final case class AppConfig(jdbc: JDBCConfig)

  object commons {

    def jdbc: Config[JDBCConfig] = deriveConfig[JDBCConfig].nested("db")

  }

  object Configuration {
    val live: ULayer[Config[AppConfig]] = ZLayer.fromZIO {

      val appConfig = for {
        jdbc <- commons.jdbc
      } yield AppConfig(jdbc)

      ZIO.succeed(appConfig)
    }
  }


}