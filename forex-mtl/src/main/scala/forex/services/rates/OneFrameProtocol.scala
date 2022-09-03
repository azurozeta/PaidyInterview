package forex.services.rates

import spray.json._

case class OneFrameRate(from: String, to: String, price: BigDecimal, ask: BigDecimal, bid: BigDecimal)

object OneFrameProtocol extends DefaultJsonProtocol {
  implicit val OneFrameRateFormat = jsonFormat5(OneFrameRate)

  def convert(json: String) : List[OneFrameRate] = {
    json.parseJson.convertTo[List[OneFrameRate]]
  }
}
