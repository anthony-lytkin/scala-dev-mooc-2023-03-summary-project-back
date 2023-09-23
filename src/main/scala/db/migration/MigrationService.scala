package db.migration

import zio._

object MigrationService {

  type MigrationService = Has[Service]

  trait Service {
    def migrate: RIO[Liqui, Unit]
  }

  object Service {
    val live: Service = new Service {
      override def migrate: RIO [Liqui, Unit] = liquibase.map(_.update(""))
    }
  }

  val live: ULayer[MigrationService] = ZLayer.succeed(Service.live)

  def migrate: RIO[Liqui, Unit] = Service.live.migrate

}
