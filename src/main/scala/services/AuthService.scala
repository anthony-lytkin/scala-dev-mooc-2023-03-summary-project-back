package services

import dao.entities._
import dao.repositories.UserRepository
import dao.repositories.UserRepository._
import db.DataSource
import dto.requests.UserAuthDTO
import dto.responses.UserDTO
import exceptions.BRException
import zio._
import _root_.logging.BRLoggerZio

object AuthService {

  type AuthService = Has[Service]

  private val logger = BRLoggerZio(this.getClass)

  trait Service {
    def authorize(login: UserAuthDTO): ZIO[DataSource, BRException, UserDTO]
  }

  class AuthServiceImpl(userRepo: UserRepository.Service) extends Service {

    override def authorize(request: UserAuthDTO): ZIO[DataSource, BRException, UserDTO] = for {
      _ <- logger.flatMap(_.info(s"Trying to authorize user with login ${request.login} ..."))
      email <- succeedOrNotFound(userRepo.findEmail(UserLogin(request.login)))(s"Пользователь ${request.login} не найден")
      userWithDep <- succeedOrNotFound(userRepo.findUserInfo(UserId(email.userId)))(s"Пользователь ${request.login} не найден")
      _ <- logger.flatMap(_.info(s"User with login ${request.login} has authorized. User id = ${userWithDep._1.id}."))
      _ <- logger.flatMap(_.debug(s"Result: $userWithDep"))
      dto <- ZIO.succeed(UserDTO(userWithDep._1, email, userWithDep._2))
    } yield dto
  }

  val service: URIO[AuthService, Service] = ZIO.environment[AuthService].map(_.get)

  val live: ZLayer[UserRepo, Nothing, AuthService] =
    ZLayer.fromService[UserRepository.Service, AuthService.Service](userRepo => new AuthServiceImpl(userRepo))

}
