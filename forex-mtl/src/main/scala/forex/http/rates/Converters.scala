package forex.http.rates

import forex.domain._
import forex.programs.rates.errors.Error
import forex.programs.rates.errors.Error.RateLookupFailed

object Converters {
  import Protocol._

  private[rates] implicit class GetApiResponseOps(val rate: Rate) extends AnyVal {
    def asGetApiResponse: GetApiResponse =
      GetApiResponse(
        from = rate.pair.from,
        to = rate.pair.to,
        price = rate.price,
        ask = rate.ask,
        bid = rate.bid,
        timestamp = rate.timestamp
      )
  }

  private[rates] implicit class GetErrorResponseOps(val error: Error) extends AnyVal {
    def asGetErrorResponse: GetErrorResponse = error match {
      case rateLookupError: RateLookupFailed => GetErrorResponse(
        error = rateLookupError.msg
      )
    }
  }

}
