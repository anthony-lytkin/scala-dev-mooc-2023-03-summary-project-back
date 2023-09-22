import exceptions.{BRCommonException, BRException, BRNotFoundException}
import zio.ZIO

package object services {

  def succeedOrException[R, E <: Throwable, A](effect: => ZIO[R, E, A]): ZIO[R, BRException, A] =
    effect.foldM(e => ZIO.fail(new BRCommonException(e.getMessage, e)), a => ZIO.succeed(a))

  def succeedOrSpecificException[R, E <: Throwable, A](effect: => ZIO[R, E, A])(exception: BRException): ZIO[R, BRException, A] =
    effect.foldM(_ => ZIO.fail(exception), a => ZIO.succeed(a))

  def succeedOrNotFound[R, E <: Exception, A](effect: => ZIO[R, E, Option[A]])(message: String): ZIO[R, BRException, A] =
    succeedOrException(effect).map(_.getOrElse(throw new BRNotFoundException(message)))

}
