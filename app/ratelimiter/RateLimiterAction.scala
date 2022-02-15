package ratelimiter

import play.api.Logger
import play.api.mvc.Results.TooManyRequests
import play.api.mvc.{Action, Request, Result}
import play.api.routing.Router

import scala.concurrent.Future

case class RateLimiterAction[A](rateLimiterService: RateLimiterService)(action: Action[A]) extends Action[A] {
  def apply(request: Request[A]): Future[Result] = {
    val rateLimiter: RateLimiterT = rateLimiterService.getObject(request.attrs(Router.Attrs.HandlerDef).path)
//    println(request.remoteAddress)

    if (rateLimiter.allowRequest(1)) {
      action(request)
    }
    else {
      Future.successful(TooManyRequests("Blocked By Rate Limiter"))
    }
  }

  override def parser = action.parser

  override def executionContext = action.executionContext
}
