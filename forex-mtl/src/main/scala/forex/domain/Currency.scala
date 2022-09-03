package forex.domain

import cats.Show

sealed trait Currency

object Currency {
  case object AUD extends Currency
  case object CAD extends Currency
  case object CHF extends Currency
  case object EUR extends Currency
  case object GBP extends Currency
  case object NZD extends Currency
  case object JPY extends Currency
  case object SGD extends Currency
  case object USD extends Currency
  case object XXX extends Currency

  implicit val show: Show[Currency] = Show.show {
    case AUD => "AUD"
    case CAD => "CAD"
    case CHF => "CHF"
    case EUR => "EUR"
    case GBP => "GBP"
    case NZD => "NZD"
    case JPY => "JPY"
    case SGD => "SGD"
    case USD => "USD"
    case _ => ""
  }

  def fromString(s: String): Currency = s.toUpperCase match {
    case "AUD" => AUD
    case "CAD" => CAD
    case "CHF" => CHF
    case "EUR" => EUR
    case "GBP" => GBP
    case "NZD" => NZD
    case "JPY" => JPY
    case "SGD" => SGD
    case "USD" => USD
    case _ => XXX // without else case, program will crash if asked for unknown pair when doing type conversion
  }

  // I want a function to iterate between case class so bad. This is a very tedious and not future proof
  val getAllPairs : List[String] = {
    List(
      "AUDCAD", "AUDCHF", "AUDEUR", "AUDGBP", "AUDNZD", "AUDJPY", "AUDSGD", "AUDUSD",
      "CADAUD", "CADCHF", "CADEUR", "CADGBP", "CADNZD", "CADJPY", "CADSGD", "CADUSD",
      "CHFAUD", "CHFCAD", "CHFEUR", "CHFGBP", "CHFNZD", "CHFJPY", "CHFSGD", "CHFUSD",
      "EURAUD", "EURCAD", "EURCHF", "EURGBP", "EURNZD", "EURJPY", "EURSGD", "EURUSD",
      "GBPAUD", "GBPCAD", "GBPCHF", "GBPEUR", "GBPNZD", "GBPJPY", "GBPSGD", "GBPUSD",
      "NZDAUD", "NZDCAD", "NZDCHF", "NZDEUR", "NZDGBP", "NZDJPY", "NZDSGD", "NZDUSD",
      "JPYAUD", "JPYCAD", "JPYCHF", "JPYEUR", "JPYGBP", "JPYNZD", "JPYSGD", "JPYUSD",
      "SGDAUD", "SGDCAD", "SGDCHF", "SGDEUR", "SGDGBP", "SGDNZD", "SGDJPY", "SGDUSD",
      "USDAUD", "USDCAD", "USDCHF", "USDEUR", "USDGBP", "USDNZD", "USDJPY", "USDSGD",
    )
  }
}
