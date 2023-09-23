import db.migration.Liqui
import liquibase.Liquibase
import liquibase.servicelocator.LiquibaseService
import zio.console.Console
import zio.{ExitCode, URIO, ZIO, ZLayer}

import javax.sql.DataSource
object Main extends zio.App {

  override def run(args: List[String]): URIO[Any with Console, ExitCode] = Application.start.exitCode

}
