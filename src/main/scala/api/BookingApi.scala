package api

import api.responses._
import db.DataSource
import dto.requests.CreateBookingDTO
import dto.responses.UserBookingDTO
import services.BookingService
import services.BookingService.BookingService
import utils.RequestUtils._
import zhttp.http._

object BookingApi {

  private type BookingApiEnv = DataSource with BookingService

  val api: Http[BookingApiEnv, Nothing, Request, Response] = Http.collectZIO[Request] {
    case Method.GET -> !! / API / V1 / "department" / id / "rooms" =>
      okWithJsonArray(BookingService.service.flatMap(_.getRoomInfo(id)))
    case Method.GET -> !! / API / V1 / "user" / id / "bookings" =>
      okWithJsonArray(BookingService.service.flatMap(_.bookingsByUser(id)))
    case Method.GET -> !! / API / V1 / "room" / id / "bookingDetails" / startTime / endTime =>
      okWithJsonObject(BookingService.service.flatMap(_.meetingRoomBookingDetails(id, urlParamToLong(startTime), urlParamToLong(endTime))))

    case req @ Method.POST -> !! / API / V1 / "user" / id / "bookRoom" =>
      okWithRequestResponseObject[BookingApiEnv, CreateBookingDTO, UserBookingDTO](req)(parsedReq => BookingService.service.flatMap(_.bookRoom(id, parsedReq)))
    case Method.DELETE -> !! / API / V1 / "booking" / id =>
      ok(BookingService.service.flatMap(_.cancelBooking(id)))
  }
}
