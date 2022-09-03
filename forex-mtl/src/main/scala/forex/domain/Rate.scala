package forex.domain

case class Rate(
    pair: Rate.Pair,
    price: Price,
    ask: Price,
    bid: Price,
    timestamp: Timestamp
)

object Rate {
  final case class Pair(
      from: Currency,
      to: Currency
  )
}
