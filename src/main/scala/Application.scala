import api.{AuthApi, BookingApi, CommonApi}
import dao.repositories.BookingRepository.BookingRepo
import dao.repositories.UserRepository.UserRepo
import dao.repositories._
import db.DataSource
import services.AuthService.AuthService
import services.BookingService.BookingService
import services._
import zhttp.http.{Http, Request, Response}
import zio._


object Application {

//  type BookingAppEnv = UserRepo with AuthService with BookingRepo with BookingService
  type BookingApiEnv = BookingService with AuthService with DataSource

//  val appLayer: ZLayer[Any, Nothing, UserRepo with AuthService with BookingRepo with BookingService] =
//    UserRepository.live >+> AuthService.live ++ (BookingRepository.live >+> BookingService.live)

  val layer: ZLayer[Any, Throwable, BookingApiEnv] =
    (BookingRepository.live >>> BookingService.live) ++ (UserRepository.live >>> AuthService.live) >+> db.live

  val api: Http[BookingApiEnv, Nothing, Request, Response] = CommonApi.api ++ BookingApi.api ++ AuthApi.api
//  ++ BookingApi.api.provideLayer(BookingRepository.live >>> BookingService.live)
//  ++ BookingApi.api.provideLayer(apiLayer)

  val server: ZIO[BookingApiEnv, Throwable, Nothing] = for {
    serverConfig <- configurations.getConfig
    server <- zhttp.service.Server.start(serverConfig.get.server.port, api)
  } yield server

  val start: ZIO[Any, Throwable, Nothing] = server.provideLayer(layer)

}
