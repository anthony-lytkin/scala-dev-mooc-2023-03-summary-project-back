package db

import configurations.LiquibaseConfig
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.{ClassLoaderResourceAccessor, CompositeResourceAccessor, FileSystemResourceAccessor}
import zio._

import javax.sql.DataSource

package object migration {

  type Liqui = Has[Liquibase]

  type MigrationService = Has[Service]

  trait Service {
    def migrate: RIO[Liqui, Unit]
  }

  object Service {
    val live: Service = new Service {
      override def migrate: RIO[Liqui, Unit] = liquibase.map(_.update(""))
    }
  }

  def initLiquibase(lqConfig: LiquibaseConfig): ZManaged[DataSource, Throwable, Liquibase] = for {
    ds <- ZIO.environment[DataSource].toManaged_
    fileAccessor <- ZIO.effect(new FileSystemResourceAccessor()).toManaged_
    classLoader <- ZIO.effect(classOf[MigrationService].getClassLoader).toManaged_
    classLoaderAccessor <- ZIO.effect(new ClassLoaderResourceAccessor(classLoader)).toManaged_
    fileOpener <- ZIO.effect(new CompositeResourceAccessor(fileAccessor, classLoaderAccessor)).toManaged_
    conn <- ZManaged.makeEffect(new JdbcConnection(ds.getConnection))(c => c.close())
    liqui <- ZIO.effect(new Liquibase(lqConfig.changeLog, fileOpener, conn)).toManaged_
  } yield liqui

  val live: ZLayer[DataSource, Throwable, Liqui] =
    ZLayer.fromManaged(configurations.getConfig.toManaged_.flatMap(cfg => initLiquibase(cfg.get.liquibase)))

//  val migrationLive: ULayer[MigrationService] = ZLayer.succeed(Service.live)

  def liquibase: URIO[Liqui, Liquibase] = ZIO.service[Liquibase]

  def migrate: RIO[Liqui, Unit] = Service.live.migrate


}
