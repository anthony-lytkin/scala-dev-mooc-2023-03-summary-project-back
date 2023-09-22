package dao.repositories

import dao.entities._
import dao.entities.schema._

import io.getquill.context.ZioJdbc._
import zio._

object UserRepository {

  type UserRepo = Has[Service]

  import db.ctx._

  trait Service {
    def findEmail(login: UserLogin): QIO[Option[UserEmail]]

    def getUserInfo(userId: UserId): QIO[Option[(User, Department)]]
  }

  class UserRepositoryImpl extends Service {

    override def findEmail(login: UserLogin): QIO[Option[UserEmail]] =
      run(userAccountSchema.filter(_.email == lift(login.login))).map(_.headOption)

    override def getUserInfo(userId: UserId): QIO[Option[(User, Department)]] = run {
      for {
        user <- userSchema.filter(_.id == lift(userId.id))
        department <- departmentSchema.join(_.id == user.departmentId)
      } yield {
        (user, department)
      }
    }.map(_.headOption)

  }

  val live: ULayer[UserRepo] = ZLayer.succeed(new UserRepositoryImpl)

}
