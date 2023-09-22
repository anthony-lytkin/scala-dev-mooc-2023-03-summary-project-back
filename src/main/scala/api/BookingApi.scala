package api

import api.responses._
import dao.entities.DepartmentId
import db.DataSource
import dto.responses.MeetingRoomDTO
import exceptions.BRException
import services.BookingService.BookingService
import zhttp.http._
import zio._

object BookingApi {

  val api = Http.collectZIO[Request] {
    case Method.GET -> !! / API / V1 / "department" / id / "rooms" =>
      val result: ZIO[DataSource with BookingService, BRException, List[MeetingRoomDTO]] = for {
        service <- ZIO.environment[BookingService].map(_.get)
        rooms <- service.getRoomInfo(DepartmentId(id))
      } yield rooms
      okWithJsonArray(result)
  }
}
