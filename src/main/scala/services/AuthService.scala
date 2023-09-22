package services

import _root_.logging.BRLogger
import dao.entities._
import dao.repositories.UserRepository
import dao.repositories.UserRepository._
import db.DataSource
import dto.requests.GetUserAuthRequest
import dto.responses.UserDTO
import exceptions.BRException
import zio._

object AuthService {

  type AuthService = Has[Service]

  private val logger = BRLogger.logger(this.getClass)

  trait Service {
    def authorize(login: GetUserAuthRequest): ZIO[DataSource, BRException, UserDTO]
  }

  class AuthServiceImpl(userRepo: UserRepository.Service) extends Service {

    override def authorize(login: GetUserAuthRequest): ZIO[DataSource, BRException, UserDTO] = for {
      _ <- logger.debug(s"Trying to authorize user with login ${login.login} ...")
      email <- succeedOrNotFound(userRepo.findEmail(UserLogin(login.login)))(s"Пользователь ${login.login} не найден")
      userWithDep <- succeedOrNotFound(userRepo.getUserInfo(UserId(email.userId)))(s"Пользователь ${login.login} не найден")
      _ <- logger.debug(s"User with login ${login.login} has authorized. User id = ${userWithDep._1.id}.")
      dto <- ZIO.succeed(UserDTO(userWithDep._1, email, userWithDep._2))
    } yield dto
  }


  val live: ZLayer[UserRepo, Nothing, AuthService] =
    ZLayer.fromService[UserRepository.Service, AuthService.Service](userRepo => new AuthServiceImpl(userRepo))

}
