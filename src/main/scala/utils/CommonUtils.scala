package utils

import java.time.{LocalDateTime, ZoneId, ZonedDateTime}

object CommonUtils {

  object dateConverters {
    implicit val localDateTime2Long: LocalDateTime => Long = (dt: LocalDateTime) => {
      val zonedDt = ZonedDateTime.of(dt, ZoneId.systemDefault())
      zonedDt.toInstant.toEpochMilli
    }
  }

}
