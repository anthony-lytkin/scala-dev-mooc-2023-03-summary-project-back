package utils

import java.time._
import scala.language.implicitConversions

object CommonUtils {

  object dateConvertersImplicit {

    implicit def localDateTime2Long(dt: LocalDateTime): Long = ZonedDateTime.of(dt, ZoneId.systemDefault()).toInstant.toEpochMilli

    implicit def long2LocalDateTime(l: Long): LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault())

  }

}
