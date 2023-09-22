package api

import api.request._
import api.responses._
import db.DataSource
import dto.requests.GetUserAuthRequest
import dto.responses.UserDTO
import exceptions._
import io.circe.syntax.EncoderOps
import services.AuthService.AuthService
import zhttp.http._
import zio._

object AuthApi {

  val api = Http.collectZIO[Request] {
    case req @ Method.POST -> !! / API / V1 / "auth" =>
      val result: ZIO[DataSource with Has[AuthService], BRException, UserDTO] = for {
        service <- ZIO.service[AuthService].map(_.get)
        dto <- request.parseRequest[GetUserAuthRequest](req).mapError(e => throw new BRBadRequestException(e))
        user <- service.authorize(dto)
      } yield user
      okWithJsonObject(result)
  }

}
