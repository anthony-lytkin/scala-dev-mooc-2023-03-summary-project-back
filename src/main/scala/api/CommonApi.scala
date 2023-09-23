package api

import api.responses.okWithText
import zhttp.http._
import zio.ZIO

object CommonApi {

  val api: Http[Any, Nothing, Request, Response] = Http.collectZIO[Request] {
    case Method.GET -> !! / API / V1 / "healthcheck" => okWithText(ZIO.succeed("Status Ok!"))
  }

}
