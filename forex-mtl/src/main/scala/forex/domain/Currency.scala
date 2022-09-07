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

  // A better approach, at least only need to add new currency here, less prone to human mistake
  val validCurrencies : List[Currency] =
    List(
      AUD,
      CAD,
      CHF,
      EUR,
      GBP,
      NZD,
      JPY,
      SGD,
      USD,
    )

  val getAllPairs : List[String] =
    for {
      a <- validCurrencies.map(x => x)
      b <- validCurrencies.map(x => x)
      if a != b
    } yield Currency.show.show(a) + Currency.show.show(b)
}
