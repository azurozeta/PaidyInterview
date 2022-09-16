package forex.services.rates

import forex.domain.Rate.Pair
import forex.domain.{Currency, Price, Rate, Timestamp}
import forex.stream.RateStream

import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object RateCache {
  type Cache = scala.collection.mutable.Map[Rate.Pair, Rate]
  var lastRefresh: Timestamp = Timestamp(OffsetDateTime.MIN)
  var rateCache: Cache = scala.collection.mutable.Map()
  val refreshIntervalSeconds: Int = 90

  def retrieveRate(pair: Rate.Pair): Option[Rate] = {
    if (ChronoUnit.SECONDS.between(lastRefresh.value, Timestamp.now.value) > refreshIntervalSeconds) {
      refreshCurrencyRate()
    }

    rateCache.get(pair)
  }

  def refreshCurrencyRate(): Unit = {
    // Will need to research more on how to implement onComplete here. For now, do blocking.
    val json = Await.result(OneFrameClient.sendRequest(), Duration(10, TimeUnit.SECONDS))

    OneFrameProtocol.convert(json).map(rate => {
      val pair = Pair(Currency.fromString(rate.from), Currency.fromString(rate.to))
      rateCache.update(pair, Rate(pair, Price(rate.price), Price(rate.ask), Price(rate.bid), Timestamp.now))
    })

    lastRefresh = Timestamp.now

    RateStream.publishRate(json)
  }
}
