import api._
import dao.repositories._
import db.DataSource
import services.AuthService.AuthService
import services.BookingService.BookingService
import services._
import zhttp.http
import zhttp.http.{Http, Middleware, Request, Response}
import zio._
import zio.logging._


object Application {

  val authApiLayer: ZLayer[Any, Throwable, AuthService with DataSource] =
    (UserRepository.live >>> AuthService.live) >+> db.live

  val bookingApiLayer: ZLayer[Any, Throwable, BookingService with DataSource] =
    (BookingRepository.live >>> BookingService.live) >+> db.live

  val loggingLayer: ZLayer[Logging, Nothing, Logging] = Logging.any >>> Logging.withRootLoggerName("booking-room-service")

  type ApiEnv = DataSource with AuthService with BookingService

  val layer: ZLayer[Any, Throwable, ApiEnv] =
    (BookingRepository.live >>> BookingService.live) ++ (UserRepository.live >>> AuthService.live) >+> db.live

  val api: Http[ApiEnv, Nothing, Request, Response] = CommonApi.api ++ (AuthApi.api ++ BookingApi.api)

  val server: ZIO[ApiEnv, Throwable, Nothing] = for {
    serverConfig <- configurations.getConfig
    server <- zhttp.service.Server.start(serverConfig.get.server.port, api @@ Middleware.cors(cors.config))
  } yield server

  val start: ZIO[Any, Throwable, Nothing] = server.provideLayer(layer)

}
