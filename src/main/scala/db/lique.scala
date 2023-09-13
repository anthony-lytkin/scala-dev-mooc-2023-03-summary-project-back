package db

import configurations.LiquibaseConfig
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.{ClassLoaderResourceAccessor, CompositeResourceAccessor, FileSystemResourceAccessor}
import zio._

import javax.sql.DataSource

object Liquibase {

  type Liqui = Has[Liquibase]

  type LiquibaseService = Has[LiquiImpl]

  trait Service {
    def migrate: RIO[Liqui, Unit]
  }

  class LiquiImpl extends Service {
    override def migrate: RIO[Liqui, Unit] = liquibase.map(_.update(""))
  }

  private def liquibase: URIO[Has[Liquibase], Liquibase] = ZIO.service[Liquibase]

  def initLiquibase(lqConfig: LiquibaseConfig): ZManaged[DataSource, Throwable, Liquibase] = for {
    ds <- ZIO.environment[DataSource].toManaged_
    fileAccessor <- ZIO.effect(new FileSystemResourceAccessor()).toManaged_
    classLoader <- ZIO.effect(classOf[LiquibaseService].getClassLoader).toManaged_
    classLoaderAccessor <- ZIO.effect(new ClassLoaderResourceAccessor(classLoader)).toManaged_
    fileOpener <- ZIO.effect(new CompositeResourceAccessor(fileAccessor, classLoaderAccessor)).toManaged_
    conn <- ZManaged.makeEffect(new JdbcConnection(ds.getConnection))(c => c.close())
    liqui <- ZIO.effect(new Liquibase(lqConfig.changeLogPath, fileOpener, conn)).toManaged_
  } yield liqui

  val liquibaseLayer: ZLayer[DataSource, Throwable, Liqui] =
    ZLayer.fromManaged(configurations.loadLiquibseConfig.toManaged_.flatMap(cfg => initLiquibase(cfg.get)))

  val live: ULayer[LiquibaseService] = ZLayer.succeed(new LiquiImpl)

}
