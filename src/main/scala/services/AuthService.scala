package services

import _root_.logging.BRLogger
import dao.entities._
import dao.repositories.UserRepository
import dao.repositories.UserRepository._
import db.DataSource
import dto.responses.UserDTO
import exceptions.BRException
import zio._

object AuthService {

  type AuthService = Has[Service]

  private val logger = BRLogger.logger(this.getClass)

  trait Service {
    def authorize(login: String): ZIO[DataSource, BRException, UserDTO]
  }

  class AuthServiceImpl(userRepo: UserRepository.Service) extends Service {

    override def authorize(login: String): ZIO[DataSource, BRException, UserDTO] = for {
      _ <- logger.debug(s"Trying to authorize user with login ${login} ...")
      email <- succeedOrNotFound(userRepo.findEmail(UserLogin(login)))(s"Пользователь ${login} не найден")
      userWithDep <- succeedOrNotFound(userRepo.findUserInfo(UserId(email.userId)))(s"Пользователь ${login} не найден")
      _ <- logger.debug(s"User with login ${login} has authorized. User id = ${userWithDep._1.id}.")
      dto <- ZIO.succeed(UserDTO(userWithDep._1, email, userWithDep._2))
    } yield dto
  }

  val service: URIO[AuthService, Service] = ZIO.environment[AuthService].map(_.get)

  val live: ZLayer[UserRepo, Nothing, AuthService] =
    ZLayer.fromService[UserRepository.Service, AuthService.Service](userRepo => new AuthServiceImpl(userRepo))

}
