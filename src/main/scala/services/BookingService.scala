package services

import dao.entities._
import dao.repositories.BookingRepository
import dao.repositories.BookingRepository.BookingRepo
import db.DataSource
import dto.responses.MeetingRoomDTO
import exceptions.BRException
import logging.BRLogger
import zio.{Has, ZIO, ZLayer}

import java.sql.SQLException

object BookingService {

  type BookingService = Has[Service]

  private val logger = BRLogger.logger(this.getClass)

  trait Service {
    def getRoomInfo(depId: DepartmentId): ZIO[DataSource, BRException, List[MeetingRoomDTO]]
  }

  class BookingServiceImpl(bookingRepo: BookingRepository.Service) extends Service {

    override def getRoomInfo(depId: DepartmentId): ZIO[DataSource, BRException, List[MeetingRoomDTO]] = for {
      _ <- logger.debug(s"Trying to get rooms for department with id ${depId.id}")
      rooms <- succeedOrException(bookingRepo.listMeetingRoomsByDepartment(depId))
      _ <- logger.debug(s"Result $rooms")
    } yield rooms.map(MeetingRoomDTO.apply)

  }

  val live: ZLayer[BookingRepo, Nothing, BookingService] =
    ZLayer.fromService[BookingRepository.Service, BookingService.Service](bookingRepo => new BookingServiceImpl(bookingRepo))

}
