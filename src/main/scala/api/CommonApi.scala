package api

import zhttp.http._
import api.API

object CommonApi {

  val api: Http[Any, Nothing, Request, Response] = Http.collectZIO[Request] {
    case Method.GET -> !! / API / V1 / "healthcheck" => responses.okWithText("Status Ok!")
  }

}
