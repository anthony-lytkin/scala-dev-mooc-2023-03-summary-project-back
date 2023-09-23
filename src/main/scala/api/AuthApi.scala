package api

import api.responses._
import dao.entities.UserLogin
import dto.requests.UserAuthDTO
import services.AuthService
import zhttp.http._

object AuthApi {

  val api = Http.collectZIO[Request] {
    case req @ Method.POST -> !! / API / V1 / "auth" => for {
      login <- request.parseRequest[UserAuthDTO](req).map(_.login)
      user <- AuthService.service.map(_.authorize(login))
      response <- okWithJsonObject(user)
    } yield response
  }

}
