package ratelimiter.implementations

import ratelimiter.RateLimiterT

class TokenBucket(val request_limit: Long, val period_millis: Long) extends RateLimiterT {
  var currentBucketSize: Long = request_limit
  var lastRefillTS: Long = System.currentTimeMillis()

  def refill(): Unit = {
    val curTS: Long = System.currentTimeMillis()
    if(curTS > lastRefillTS + period_millis) {
      lastRefillTS = curTS
      currentBucketSize = request_limit
    }
  }

  def allowRequest(): Boolean = {
    this.synchronized {
      refill()
      if (currentBucketSize >= 1) {
        currentBucketSize -= 1
        true
      }
      else {
        false
      }
    }
  }

  def noOfRequestLeft(): Long = {
    currentBucketSize
  }

}
