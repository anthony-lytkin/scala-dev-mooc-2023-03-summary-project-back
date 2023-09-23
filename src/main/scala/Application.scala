import api.{AuthApi, BookingApi, CommonApi}
import dao.repositories.BookingRepository.BookingRepo
import dao.repositories._
import db.DataSource
import services.AuthService.AuthService
import services.BookingService.BookingService
import services._
import zhttp.http.{Http, Request, Response}
import zio._


object Application {

//  type BookingAppEnv = UserRepo with AuthService with BookingRepo with BookingService
  type BookingApiEnv = BookingService with DataSource

//  val appLayer: ZLayer[Any, Nothing, UserRepo with AuthService with BookingRepo with BookingService] =
//    UserRepository.live >+> AuthService.live ++ (BookingRepository.live >+> BookingService.live)

  val layer: ZLayer[Any, Throwable, BookingService with DataSource] =
    (BookingRepository.live >>> BookingService.live) >+> db.live
//  (UserRepository.live >>> AuthService.live)

  val api: Http[DataSource with BookingService, Nothing, Request, Response] = CommonApi.api ++ BookingApi.api
//  ++ AuthApi.api // todo Добавить AuthApi

  val server: ZIO[BookingApiEnv, Throwable, Nothing] = for {
    serverConfig <- configurations.getConfig
    server <- zhttp.service.Server.start(serverConfig.get.server.port, api)
  } yield server

  val start: ZIO[Any, Throwable, Nothing] = server.provideLayer(layer)

}
