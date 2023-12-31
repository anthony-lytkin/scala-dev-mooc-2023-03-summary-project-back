package services

import _root_.logging._
import dao.entities._
import dao.repositories.BookingRepository
import dao.repositories.BookingRepository.BookingRepo
import db.DataSource
import dto.requests.CreateBookingDTO
import dto.responses.{BookingRoomDetailsDTO, MeetingRoomDTO, UserBookingDTO}
import exceptions.{BRCommonException, BRException, BRNotFoundException}
import utils.CommonUtils.dateConvertersImplicit._
import vo.BookingVO
import zio._

object BookingService {

  type BookingService = Has[Service]

  import db.ctx._


  private val logger: UIO[BRLogger] = BRLoggerZio(this.getClass)

  trait Service {
    def getRoomInfo(depId: String): ZIO[DataSource, BRException, List[MeetingRoomDTO]]

    def bookingsByUser(userId: String): ZIO[DataSource, BRException, List[UserBookingDTO]]

    def meetingRoomBookingDetails(roomId: String, startTime: Long, endTime: Long): ZIO[DataSource, BRException, BookingRoomDetailsDTO]

    def bookRoom(userId: String, request: CreateBookingDTO): ZIO[DataSource, BRException, UserBookingDTO]

    def cancelBooking(bookingId: String): ZIO[DataSource, BRException, Unit]
  }

  class BookingServiceImpl(bookingRepo: BookingRepository.Service) extends Service {

    override def getRoomInfo(depId: String): ZIO[DataSource, BRException, List[MeetingRoomDTO]] = for {
      _ <- logger.flatMap(_.debug(s"Trying to get rooms for department with id $depId"))
      rooms <- succeedOrException(bookingRepo.listMeetingRoomsByDepartment(DepartmentId(depId)))
      dto <- ZIO.succeed(rooms.map(MeetingRoomDTO.apply))
      _ <- logger.flatMap(_.debug(s"Result $dto"))
    } yield dto

    override def bookingsByUser(userId: String): ZIO[DataSource, BRException, List[UserBookingDTO]] = for {
      _ <- logger.flatMap(_.debug(s"Trying to get user's bookings for user with id $userId"))
      bookings <- succeedOrException(bookingRepo.findBookingsByUser(UserId(userId)))
      dto <- ZIO.succeed(bookings.map(UserBookingDTO.apply))
      _ <- logger.flatMap(_.debug(s"Result $dto"))
    } yield dto

    override def meetingRoomBookingDetails(roomId: String, startTime: Long, endTime: Long): ZIO[DataSource, BRException, BookingRoomDetailsDTO] = for {
      _ <- logger.flatMap(_.debug(s"Trying to get meeting room booking details for meeting room with id $roomId ..."))
      room <- succeedOrNotFound(bookingRepo.findMeetingRoom(RoomId(roomId)))((s"Переговорная комната не найдена."))
      bookings <- succeedOrException(bookingRepo.listBookingByRoom(BookingVO(roomId, startTime, endTime)))
      bookingsVO <- for {
        vo <- ZIO.succeed(bookings.map(b => BookingVO(b._1, b._2)))
      } yield vo
      dto <- ZIO.succeed(BookingRoomDetailsDTO.apply(room, bookingsVO))
      _ <- logger.flatMap(_.debug(s"Result: $dto"))
    } yield dto

    override def bookRoom(userId: String, request: CreateBookingDTO): ZIO[DataSource, BRException, UserBookingDTO] = for {
      _ <- logger.flatMap(_.info(s"Booking room with id ${request.roomId} ..."))
      newBooking <- succeedOrException {
        transaction {
          for {
            existingBooking <- bookingRepo.findBookingByRoomIdAndBookingTime(BookingVO(request))
            booking <- existingBooking.fold {
              for {
                uuid <- generateUUID
                newBooking <- bookingRepo.insertBooking(Booking(uuid, userId, request.roomId, request.startTime, request.endTime))
                _ <- logger.flatMap(_.info(s"Meeting room ${request.roomId} was booked. Book id $uuid"))
              } yield newBooking
            }(_ => ZIO.fail(throw new BRNotFoundException("Не удалось найти свободное время на указанные даты. Выберете другое время.")))
          } yield booking
        }
      }
      bookingWithRoom <- succeedOptOrSpecificException(bookingRepo.findBooking(BookingId(newBooking.id))) {
        new BRCommonException("Произошла непредвиденная ошибка при бронировании. Попробуйте забронировать комнату позже.")
      }
      dto <- ZIO.succeed(UserBookingDTO.apply(bookingWithRoom._1, bookingWithRoom._2))
      _ <- logger.flatMap(_.debug(s"Result: $dto"))
    } yield dto

    override def cancelBooking(bookingId: String): ZIO[DataSource, BRException, Unit] = for {
      _ <- logger.flatMap(_.info(s"Trying to cancel booking with id $bookingId ..."))
      _ <- succeedOrException {
        transaction {
          for {
            _ <- succeedOrNotFound(bookingRepo.findBooking(BookingId(bookingId)))("Бронирование не найдено.")
            _ <- bookingRepo.deleteBooking(BookingId(bookingId))
            _ <- logger.flatMap(_.info(s"Booking with id $bookingId was successfully canceled."))
          } yield ()
        }
      }
    } yield ()
  }

  val service: URIO[BookingService, Service] = ZIO.environment[BookingService].map(_.get)

  val live: ZLayer[BookingRepo, Nothing, BookingService] =
    ZLayer.fromService[BookingRepository.Service, BookingService.Service](bookingRepo => new BookingServiceImpl(bookingRepo))

}
