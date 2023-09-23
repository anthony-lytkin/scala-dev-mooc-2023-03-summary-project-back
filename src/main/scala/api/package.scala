import db.DataSource
import dto._
import dto.errors.ErrorDTO
import exceptions._
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, jawn}
import zhttp.http._
import zio._

import scala.language.implicitConversions
import scala.reflect.runtime.universe.Try

package object api {

  val API = "api"
  val V1 = "v1"

  private[api] object responses {

    def handleRequestError(e: Throwable): UIO[Response] = ZIO.succeed {
      implicit def tohttpData(error: ErrorDTO): HttpData = HttpData.fromString(ErrorDTO.encoder(error).toString())
      e match {
        case nfe: BRNotFoundException => Response(Status.NotFound, Headers.empty, ErrorDTO.notFoundError(nfe))
        case bre: BRBadRequestException => Response(Status.BadRequest, Headers.empty, ErrorDTO.badRequest(bre))
        case ce: BRCommonException => Response(Status.InternalServerError, Headers.empty, ErrorDTO.internalServerError(ce))
        case e: BRException => Response(Status.InternalServerError, Headers.empty, ErrorDTO.internalServerError(e))
        case throwable: Throwable => Response(Status.InternalServerError, Headers.empty, ErrorDTO.fromThrowable(throwable))
      }
    }

    private def handleResultEmpty[R, E <: BRException, Any](effect: ZIO[R, E, Any]): URIO[R, Response] =
      effect.foldM(handleRequestError, _ => ZIO.succeed(Response.ok))

    private def handleResultText[R, E <: BRException, String](effect: ZIO[R, E, String]): URIO[R, Response] =
      effect.foldM(handleRequestError, a => ZIO.succeed(Response.text(a.toString)))

    private def handleResultObject[R, E <: BRException, A <: ResponseDTO](effect: ZIO[R, E, A])(implicit encoder: Encoder[A]): URIO[R, Response] =
      effect.foldM(handleRequestError, a => ZIO.succeed(Response.json(a.asJson.toString())))

    private def handleResultArray[R, E <: BRException, A <: ResponseDTO](effect: ZIO[R, E, Seq[A]])(implicit encoder: Encoder[A]): URIO[R, Response] =
      effect.foldM(handleRequestError, a => ZIO.succeed(Response.json(a.asJson.toString())))

    def ok[R](effect: => ZIO[R, BRException, Any]): URIO[R, Response] =
      handleResultEmpty(effect)

    def okWithText[R](effect: => ZIO[R, BRException, String]): URIO[R, Response] =
      handleResultText(effect)

    def okWithJsonObject[R, T <: ResponseDTO](effect: => ZIO[R, BRException, T])(implicit encoder: Encoder[T]): URIO[R, Response] =
      handleResultObject(effect)

    def okWithRequestResponseObject[R, B <: RequestDTO, T <: ResponseDTO](requested: Request)(effect: B => ZIO[R, BRException, T])
                                                                         (implicit encoder: Encoder[T], decoder: Decoder[B]): URIO[R, Response] = {
      request.parseRequest(requested).foldM(
        e => ZIO.environment[R] zipRight handleRequestError(e),
        b => handleResultObject(effect(b))
      )
    }

    def okWithJsonArray[R, T <: ResponseDTO](data: ZIO[R, BRException, List[T]])(implicit encoder: Encoder[T]): URIO[R, Response] =
      handleResultArray(data)

  }

  object request {

    def parseRequest[A <: RequestDTO](request: Request)(implicit decoder: Decoder[A]): ZIO[Any, BRBadRequestException, A] = {
      val dto: ZIO[Any, Throwable, A] = for {
        body <- request.body
        json <- ZIO.fromEither(jawn.parse(body.toString()))
        dto <- ZIO.fromEither(decoder.decodeJson(json))
      } yield dto
      dto.foldM(e => ZIO.fail(new BRBadRequestException(e)), ZIO.succeed(_))
    }
  }

}

//val config: CorsConfig =
//  CorsConfig(
//    allowedOrigin = {
//      case origin@Origin.Value(_, host, _) if host == "localhost" =>
//        Some(AccessControlAllowOrigin.Specific(origin))
//      case _ => None
//    },
//  )