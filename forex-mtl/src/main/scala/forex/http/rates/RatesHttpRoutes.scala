package forex.http
package rates

import cats.effect.Sync
import cats.syntax.flatMap._
import forex.programs.RatesProgram
import forex.programs.rates.{ Protocol => RatesProgramProtocol }
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

class RatesHttpRoutes[F[_]: Sync](rates: RatesProgram[F]) extends Http4sDsl[F] {

  import Converters._, QueryParams._, Protocol._

  private[http] val prefixPath = "/rates"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root :? FromQueryParam(from) +& ToQueryParam(to) =>
      rates.get(RatesProgramProtocol.GetRatesRequest(from, to)).flatMap(Sync[F].fromEither).flatMap { rate =>
        // The last flatMap actually crash and throw this error if i put the OneFrameLookupFailed error
        // Not sure what's missing, so close yet so far
        // forex.programs.rates.errors$Error$RateLookupFailed: null
        //   at forex.programs.rates.errors$.toProgramError(errors.scala:13)
        Ok(rate.asGetApiResponse)
      }
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

}
