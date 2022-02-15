package ratelimiter.implementations

import ratelimiter.RateLimiterT

class TokenBucket(val noOfRequest: Long, val refillRate: Long) extends RateLimiterT {
  var currentBucketSize: Long = noOfRequest
  var lastRefillTS: Long = System.currentTimeMillis()

  def refill(): Unit = {
    val curTS: Long = System.currentTimeMillis()
    if(curTS > lastRefillTS + refillRate) {
      lastRefillTS = curTS
      currentBucketSize = noOfRequest
    }
  }

  def allowRequest(tokens: Long): Boolean = {
    this.synchronized {
      refill()
      if (currentBucketSize >= tokens) {
        currentBucketSize -= tokens
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
