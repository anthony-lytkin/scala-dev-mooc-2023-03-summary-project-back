import zio.console.Console
import zio._

object Main extends App {

  override def run(args: List[String]): URIO[Any with Console, ExitCode] = Application.start.exitCode

}
