import org.scalatest._
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import play.api.Application
import play.api.http.Status.{OK, TOO_MANY_REQUESTS}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.{WSClient, WSRequest}
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


class RateLimiterSpec extends PlaySpec with GuiceOneServerPerTest {

  override def newAppForTest(td: TestData): Application = {
    GuiceApplicationBuilder().build()
  }

  def testRateLimitForSingleEntity(entity: String): Unit = {
    val limit = app.configuration.get[String](raw"""rate-limiter-key-value-config."/$entity/$$id<[^/]+>".request-limit""").toInt
    val wsClient = app.injector.instanceOf[WSClient]
    val myPublicAddress = s"localhost:$port"
    val api = s"http://$myPublicAddress/$entity/1"
    val futureOperations: List[Future[WSRequest#Response]] = List.fill(limit)(wsClient.url(api).get())

    val futureFoldLeft = Future.foldLeft(futureOperations)(0L){ case (sum, request) =>
      sum + (if (request.status == OK) 1 else 0)
    }
    futureFoldLeft.onComplete {
      case Success(results) => println(s"Results $results")
      case Failure(e)       => println(s"Error processing future operations, error = ${e.getMessage}")
    }
    await(futureFoldLeft) mustBe limit

    await(wsClient.url(api).get()).status mustBe TOO_MANY_REQUESTS
  }

  def testRateLimitForBothEntity(): Unit = {
    val limit1 = app.configuration.get[String](raw"""rate-limiter-key-value-config."/city/$$id<[^/]+>".request-limit""").toInt
    val limit2 = app.configuration.get[String](raw"""rate-limiter-key-value-config."/room/$$id<[^/]+>".request-limit""").toInt
    val limit = limit1 + limit2

    val wsClient = app.injector.instanceOf[WSClient]
    val myPublicAddress = s"localhost:$port"
    val api1 = s"http://$myPublicAddress/city/1"
    val api2 = s"http://$myPublicAddress/room/1"
    val futureOperations: List[Future[WSRequest#Response]] = List.fill(limit)(wsClient.url(api1).get()) ::: List.fill(limit)(wsClient.url(api2).get())

    val futureFoldLeft = Future.foldLeft(futureOperations)(0L){ case (sum, request) =>
      sum + (if (request.status == OK) 1 else 0)
    }
    futureFoldLeft.onComplete {
      case Success(results) => println(s"Results $results")
      case Failure(e)       => println(s"Error processing future operations, error = ${e.getMessage}")
    }
    await(futureFoldLeft) mustBe limit

    await(wsClient.url(api1).get()).status mustBe TOO_MANY_REQUESTS
    await(wsClient.url(api2).get()).status mustBe TOO_MANY_REQUESTS
  }

  "(n + 1)th request for city endpoint is TOO_MANY_REQUESTS given that limit is n in config" in {
    testRateLimitForSingleEntity("city")
  }
  "(n + 1)th request for room endpoint is TOO_MANY_REQUESTS given that limit is n in config" in {
    testRateLimitForSingleEntity("room")
  }
  "(n + 1)th request for both city and room endpoint is TOO_MANY_REQUESTS given that limit is n in config" in {
    testRateLimitForSingleEntity("room")
  }
  "refill works properly given that policy is dynamic" in {
    val entity = "city"
    val sleep_limit = app.configuration.get[String](raw"""rate-limiter-key-value-config."/$entity/$$id<[^/]+>".period-millis""").toInt
    testRateLimitForSingleEntity(entity)
    Thread.sleep(sleep_limit + 5) // in dynamic policy -> refill happens upon getting first request after a while
    testRateLimitForSingleEntity(entity)
  }
}
