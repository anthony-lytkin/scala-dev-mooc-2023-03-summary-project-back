import org.slf4j.{Logger, LoggerFactory}
import zio._

package object logging {

  type BRLoggerService = Has[LoggingService]
  type BRLogger = ServiceImpl

  trait LoggingService {

    def info(message: String): UIO[Unit]

    def debug(message: String): UIO[Unit]

    def error(message: String): UIO[Unit]

  }

  class ServiceImpl(logger: Logger) extends LoggingService {

    override def info(message: String): UIO[Unit] = ZIO.succeed(logger.info(message))

    override def debug(message: String): UIO[Unit] = ZIO.succeed(logger.debug(message))

    override def error(message: String): UIO[Unit] = ZIO.succeed(logger.debug(message))

  }

  def BRLoggerLayer(clazz: Class[_]): ULayer[BRLoggerService] = ZLayer.succeed(new ServiceImpl(LoggerFactory.getLogger(clazz.getClass)))

  def BRLoggerZio(clazz: Class[_]): UIO[BRLogger] = ZIO.succeed(new ServiceImpl(LoggerFactory.getLogger(clazz.getClass)))

}
