package forex.services.rates

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import forex.config.ApplicationConfig
import forex.domain.Currency

import scala.concurrent.Future
import scala.concurrent.duration.{Duration, SECONDS}

object OneFrameClient {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  // Haven't figured out how to make this receive parameter while immutable.
  // I don't want to reread the config file.
  var host: String = ""
  var port: Int = 0
  val token: String = "10dc303535874aeccc86a8251e6992f5"

  val pairs = Currency.getAllPairs.map(pair => "pair=" + pair).mkString("&")

  def apply(config: ApplicationConfig): Unit = {
    host = config.oneFrame.host
    port = config.oneFrame.port
  }

  def sendRequest(): Future[String] = {
    val rateRequest = HttpRequest(
      method = HttpMethods.GET,
      uri = Uri("http://" + host + ":" + port.toString + "/rates?" + pairs)
    ).withHeaders(
      headers.RawHeader("token", token)
    )

    val responseFuture: Future[HttpResponse] = Http().singleRequest(rateRequest)
    val entityFuture: Future[HttpEntity.Strict] = responseFuture.flatMap(response => response.entity.toStrict(Duration(5, SECONDS)))
    entityFuture.map(entity => entity.data.utf8String)
  }
}
