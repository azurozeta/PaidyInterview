import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.EitherValues
import forex.services.rates.interpreters.OneFrame
import cats.effect._
import forex.domain.Currency._
import forex.domain.Rate
import forex.domain.Rate.Pair
import forex.services.rates.OneFrameClient

class OneFrameSpec extends AnyWordSpec with Matchers with EitherValues {
  val oneFrame = new OneFrame[IO]
  // TODO: try to read from config later or research how to mock it
  OneFrameClient.host = "0.0.0.0"
  OneFrameClient.port = 8085

  "One Frame" should {
    "return a valid Rate" in {
      val validPair = Pair(USD, JPY)
      oneFrame.get(validPair).map(x => x.getOrElse(fail("one frame didn't give valid rate!")) shouldBe a[Rate])
    }
    "return a look up error" in {
      val invalidPair = Pair(USD, XXX)
      oneFrame.get(invalidPair).map(x => x.getOrElse(fail("one frame didn't give a look up error!")) shouldBe a[Error])
    }
  }
}