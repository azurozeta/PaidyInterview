package forex.services.rates.interpreters

import forex.services.rates.{Algebra, RateCache}
import cats.Applicative
import cats.syntax.applicative._
import cats.syntax.either._
import forex.domain._
import forex.services.rates.errors.Error.OneFrameLookupFailed
import forex.services.rates.errors._

class OneFrame[F[_]: Applicative] extends Algebra[F] {
  override def get(pair: Rate.Pair): F[Error Either Rate] = {
    fetchRate(pair).pure[F]
  }

  def fetchRate(pair: Rate.Pair) : Either[Error, Rate] = {
    RateCache.retrieveRate(pair) match {
      case None => Either.left(OneFrameLookupFailed("Can not find matching pair currency."))
      case Some(s: Rate) => Either.right(s)
    }
  }
}
