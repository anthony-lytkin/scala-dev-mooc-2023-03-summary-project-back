import dto._
import dto.errors.ErrorDTO
import exceptions._
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, jawn}
import zhttp.http._
import zio._

import scala.language.implicitConversions

package object api {

  val API = "api"
  val V1 = "v1"

  object responses {

    private def handlerError(e: Throwable): UIO[Response] = ZIO.succeed {
      implicit def tohttpData(error: ErrorDTO): HttpData = HttpData.fromString(ErrorDTO.encoder(error).toString())
      e match {
        case nfe: BRNotFoundException => Response(Status.NotFound, Headers.empty, ErrorDTO.notFoundError(nfe))
        case bre: BRBadRequestException => Response(Status.BadRequest, Headers.empty, ErrorDTO.badRequest(bre))
        case ce: BRCommonException => Response(Status.InternalServerError, Headers.empty, ErrorDTO.internalServerError(ce))
        case e: BRException => Response(Status.InternalServerError, Headers.empty, ErrorDTO.internalServerError(e))
        case throwable: Throwable => Response(Status.InternalServerError, Headers.empty, ErrorDTO.fromThrowable(throwable))
      }
    }

    private def handleResultObject[R, E <: BRException, A <: ResponseDTO](effect: ZIO[R, E, A])(implicit encoder: Encoder[A]): URIO[R, Response] = {
      effect.foldM(
        handlerError,
        a => ZIO.succeed(Response.json(a.asJson.toString()))
      )
    }

    private def handleResultArray[R, E <: BRException, A <: ResponseDTO](effect: ZIO[R, E, Seq[A]])(implicit encoder: Encoder[A]): URIO[R, Response] =
      effect.foldM(
        handlerError,
        a => ZIO.succeed(Response.json(a.asJson.toString()))
      )

    def okWithText(message: String): UIO[Response] =
      ZIO.succeed(Response.apply(Status.Ok, Headers.empty, HttpData.fromString(message)))

    def okWithJsonObject[R, T <: ResponseDTO](data: ZIO[R, BRException, T])(implicit encoder: Encoder[T]): URIO[R, Response] =
      handleResultObject(data)

    def okWithJsonArray[R, T <: ResponseDTO](data: ZIO[R, BRException, List[T]])(implicit encoder: Encoder[T]): URIO[R, Response] =
      handleResultArray(data)

    def okWithJsonOrNotFound[R, T <: ResponseDTO](data: ZIO[R, BRNotFoundException, T])(implicit encoder: Encoder[T]): URIO[R, Response] = {
      handleResultObject(data)
    }

  }

  object request {

    def parseRequest[A <: RequestDTO](request: Request)(implicit decoder: Decoder[A]): ZIO[Any, BRBadRequestException, A] = {
      val dto: ZIO[Any, Throwable, A] = for {
        body <- request.body
        json <- ZIO.fromEither(jawn.parse(body.toString()))
        dto <- ZIO.fromEither(decoder.decodeJson(json))
        //        dto <- ZIO.fromEither(decoded).mapError(e => throw new BRBadRequestException("Ошибка валидации", e))
      } yield dto
      dto.mapError(e => new BRBadRequestException(e))
    }

  }

}
