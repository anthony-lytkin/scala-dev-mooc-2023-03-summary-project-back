import zio.console.Console
import zio.{ExitCode, URIO}
object Main extends zio.App {

  override def run(args: List[String]): URIO[Any with Console, ExitCode] = Application.start.exitCode

}
