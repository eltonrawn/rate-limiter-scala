package ratelimiter

import play.api.Logger
import play.api.mvc.Results.TooManyRequests
import play.api.mvc.{Action, BodyParser, Request, Result}
import play.api.routing.Router

import scala.concurrent.{ExecutionContext, Future}

case class RateLimiterAction[A](rateLimiterService: RateLimiterService)(action: Action[A]) extends Action[A] {
  def apply(request: Request[A]): Future[Result] = {
    val rateLimiter: RateLimiterT = rateLimiterService.getObject(request.attrs(Router.Attrs.HandlerDef).path)
//    println(request.remoteAddress)

    if (rateLimiter.allowRequest()) {
      action(request)
    }
    else {
      Future.successful(TooManyRequests("Blocked By Rate Limiter"))
    }
  }

  override def parser: BodyParser[A] = action.parser

  override def executionContext: ExecutionContext = action.executionContext
}
