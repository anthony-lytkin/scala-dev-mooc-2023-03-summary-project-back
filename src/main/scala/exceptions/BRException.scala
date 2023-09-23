package exceptions

trait BRException extends Throwable

class BRCommonException(message: String, cause: Option[Throwable]) extends java.lang.Exception(message, cause.orNull) with BRException {

  def this(message: String) = this(message, None)

  def this(message: String, cause: Throwable) = this(message, Some(cause))

}

class BRNotFoundException(message: String) extends BRCommonException(message) with BRException

class BRBadRequestException(message: String, cause: Option[Throwable]) extends BRCommonException(message, cause) with BRException {

  def this(message: String) = this(message, None)

  def this(message: String, cause: Throwable) = this(message, Some(cause))

  def this(cause: Throwable) = this(cause.getMessage, Some(cause))
}
