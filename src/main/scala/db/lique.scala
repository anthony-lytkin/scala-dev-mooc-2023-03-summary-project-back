package db

import configurations.common.{JDBCConfig, LiquibaseConfig}
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.{ClassLoaderResourceAccessor, CompositeResourceAccessor, DirectoryResourceAccessor}
import zio._
import zio.managed.ZManaged

import java.io.File
import javax.sql.DataSource

object lique {

  trait Service {

    def migrate: RIO[Liquibase, Unit]
  }

  class LiqueService extends Service {
    override def migrate: RIO[Liquibase, Unit] = liquibase.map(_.update())
  }

  private def liquibase: URIO[Liquibase, Liquibase] = ZIO.service[Liquibase]

  def createLique(config: LiquibaseConfig): ZManaged[DataSource, Throwable, Liquibase] = for {
    ds <- ZManaged.fromZIO(ZIO.environment[DataSource].map(_.get))
    fileAccessor <- ZManaged.fromZIO(ZIO.attempt(new DirectoryResourceAccessor(new File(""))))
    classLoader <- ZManaged.fromZIO(ZIO.attempt(classOf[Tag[Service]].getClassLoader))
    classLoaderAccessor <- ZManaged.fromZIO(ZIO.attempt(new ClassLoaderResourceAccessor(classLoader)))
    fileOpener <- ZManaged.fromZIO(ZIO.attempt(new CompositeResourceAccessor(fileAccessor, classLoaderAccessor)))
    conn <- ZManaged.acquireReleaseAttemptWith(new JdbcConnection(ds.getConnection()))(c => c.close())
    lique <- ZManaged.fromZIO(ZIO.attempt(new Liquibase(config.changeLog, fileOpener, conn)))
  } yield lique

  val liquibaseLayer = for {
    config <- configurations.
  }

  val live: ULayer[LiqueService] = ZLayer.succeed(new LiqueService)


}
