package utils

import exceptions.BRBadRequestException

import scala.language.implicitConversions
import scala.util.Try

object RequestUtils {

  def urlParamToLong(param: String): Long =
    Try(param.toLong).getOrElse(throw new BRBadRequestException(s"Невозможно преобразовать параметры запроса. Тип параметра $param не совпадает с Long."))

}
