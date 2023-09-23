package api

import api.responses._
import db.DataSource
import dto.requests.UserAuthDTO
import dto.responses.UserDTO
import services.AuthService
import services.AuthService.AuthService
import zhttp.http._

object AuthApi {

  private type AuthApiEnv = DataSource with AuthService

  val api = Http.collectZIO[Request] {
    case req @ Method.POST -> !! / API / V1 / "authorize" =>
      okWithRequestResponseObject[AuthApiEnv, UserAuthDTO, UserDTO](req)(parsedReq => AuthService.service.flatMap(_.authorize(parsedReq)))
  }

}
