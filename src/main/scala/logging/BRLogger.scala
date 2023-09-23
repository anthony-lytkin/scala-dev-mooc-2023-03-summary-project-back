package logging

import zio._
import org.slf4j.LoggerFactory

class BRLogger private(clazz: Class[_]) {
  val logger = LoggerFactory.getLogger(clazz)

  def info(message: String): UIO[Unit] = ZIO.succeed(logger.info(message))

  def debug(message: String): UIO[Unit] = ZIO.succeed(logger.debug(message))

  def error(message: String): UIO[Unit] = ZIO.succeed(logger.error(message))
}

object BRLogger {
  def logger(clazz: Class[_]) = new BRLogger(clazz)
}
