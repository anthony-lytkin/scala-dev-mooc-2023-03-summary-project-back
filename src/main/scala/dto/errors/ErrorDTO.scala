package dto.errors

import dto.ResponseDTO
import exceptions._
import io.circe.Encoder
import io.circe.derivation.deriveEncoder

import java.time.LocalDateTime

case class ErrorDTO(title: String, message: String, time: LocalDateTime = LocalDateTime.now()) extends ResponseDTO

object ErrorDTO {

  implicit val encoder: Encoder[ErrorDTO] = deriveEncoder

  def notFoundError(e: BRNotFoundException): ErrorDTO = ErrorDTO("Nothing was found", e.getMessage)

  def badRequest(e: BRBadRequestException): ErrorDTO = ErrorDTO("Bad Request", e.getMessage)

  def internalServerError(e: BRException): ErrorDTO = ErrorDTO("Internal server error", e.getMessage)

  def fromThrowable(e: Throwable): ErrorDTO = ErrorDTO("Unexpected error was occurred", e.getMessage)

}