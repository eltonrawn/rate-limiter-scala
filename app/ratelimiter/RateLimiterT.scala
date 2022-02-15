package ratelimiter

trait RateLimiterT {
  def allowRequest(tokens: Long): Boolean
  def noOfRequestLeft(): Long
}
