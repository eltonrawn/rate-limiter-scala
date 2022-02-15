package hotel

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import play.api.routing.Router
import ratelimiter.{RateLimiterAction, RateLimiterService}

import javax.inject.{Inject, Singleton}

@Singleton
class HotelController @Inject()(hotelService: HotelService, rateLimiterService: RateLimiterService, val controllerComponents: ControllerComponents) extends BaseController {
  val logger: Logger = Logger(this.getClass())
  def getCityById(id: String): Action[AnyContent] = RateLimiterAction(rateLimiterService) {
    Action { request =>
      val json = Json.toJson(hotelService.getColumnById(id, "CITY"))
      logger.info("no_of_request_left " + rateLimiterService.getObject(request.attrs(Router.Attrs.HandlerDef).path).noOfRequestLeft())
      Ok(json)
    }
  }
//  equivalent to
//  def getAll(): Action[AnyContent] = RateLimiterAction(rateLimiterService.get_object())(Action.apply(Ok(rateLimiterService.get_object().currentBucketSize + "")))

  def getRoomById(id: String): Action[AnyContent] = RateLimiterAction(rateLimiterService) {
    Action { request =>
      val json = Json.toJson(hotelService.getColumnById(id, "ROOM"))
      logger.info("no_of_request_left " + rateLimiterService.getObject(request.attrs(Router.Attrs.HandlerDef).path).noOfRequestLeft())
      Ok(json)
    }
  }
}