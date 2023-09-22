import org.slf4j.LoggerFactory
import zio._

package object logging {

  class BRLogger private(clazz: Class[_]) {

    val logger = LoggerFactory.getLogger(clazz)

    def info(message: String): UIO[Unit] = ZIO.succeed(logger.info(message))

    def debug(message: String): UIO[Unit] = ZIO.succeed(logger.debug(message))

    def error(message: String): UIO[Unit] = ZIO.succeed(logger.error(message))
  }

  object BRLogger {
    def logger(clazz: Class[_]) = new BRLogger(clazz)
  }

}
