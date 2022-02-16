package hotel

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import play.api.routing.Router
import ratelimiter.{RateLimiterAction, RateLimiterService}
import scala.util.{Try,Success,Failure}
import javax.inject.{Inject, Singleton}

@Singleton
class HotelController @Inject()(hotelService: HotelService, rateLimiterService: RateLimiterService, val controllerComponents: ControllerComponents) extends BaseController {
  val logger: Logger = Logger(this.getClass())
  def getCityById(id: String): Action[AnyContent] = RateLimiterAction(rateLimiterService) {
    Action { request =>
      hotelService.getColumnById(id, "CITY") match {
        case Success(value) => {
          val json = Json.toJson(value)
          logger.info("no_of_request_left " + rateLimiterService.getObject(request.attrs(Router.Attrs.HandlerDef).path).noOfRequestLeft())
          Ok(json)
        }
        case Failure(s) => InternalServerError
      }
    }
  }
//  equivalent to
//  def getAll(): Action[AnyContent] = RateLimiterAction(rateLimiterService.get_object())(Action.apply(Ok(rateLimiterService.get_object().currentBucketSize + "")))

  def getRoomById(id: String): Action[AnyContent] = RateLimiterAction(rateLimiterService) {
    Action { request =>
      hotelService.getColumnById(id, "ROOM") match {
        case Success(value) => {
          val json = Json.toJson(value)
          logger.info("no_of_request_left " + rateLimiterService.getObject(request.attrs(Router.Attrs.HandlerDef).path).noOfRequestLeft())
          Ok(json)
        }
        case Failure(s) => InternalServerError
      }
    }
  }
}