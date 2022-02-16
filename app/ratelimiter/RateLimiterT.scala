package ratelimiter

trait RateLimiterT {
  def allowRequest(): Boolean
  def noOfRequestLeft(): Long
}
