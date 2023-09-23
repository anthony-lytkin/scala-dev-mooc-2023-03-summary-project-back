import exceptions.{BRCommonException, BRException, BRNotFoundException}
import zio._

package object services {

  def generateUUID: UIO[String] = random.nextUUID.map(_.toString).provideLayer(zio.random.Random.live)

  def succeedOrException[R, E <: Throwable, A](effect: => ZIO[R, E, A]): ZIO[R, BRException, A] =
    effect.foldM(e => ZIO.fail(new BRCommonException(e.toString, e)), a => ZIO.succeed(a))

  def succeedOrSpecificException[R, E <: Throwable, A](effect: => ZIO[R, E, A])(exception: => BRException): ZIO[R, BRException, A] =
    effect.foldM(_ => ZIO.fail(exception), a => ZIO.succeed(a))

  def succeedOptOrSpecificException[R, E <: Throwable, A](effect: => ZIO[R, E, Option[A]])(exception: => BRException): ZIO[R, BRException, A] =
    effect.foldM(_ => ZIO.fail(exception), a => ZIO.succeed(a.getOrElse(throw exception)))

  def succeedOrNotFound[R, E <: Exception, A](effect: => ZIO[R, E, Option[A]])(message: String): ZIO[R, BRException, A] =
    succeedOrException(effect).map(_.getOrElse(throw new BRNotFoundException(message)))

}
