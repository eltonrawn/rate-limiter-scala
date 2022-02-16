package ratelimiter.implementations

import ratelimiter.RateLimiterT

import scala.collection.mutable

class SlidingLogs(val request_limit: Long, val period_millis: Long) extends RateLimiterT {
  val queue: mutable.Queue[Long] = mutable.Queue.empty[Long]

  def dequeue(curTS: Long): Unit = {
    while(queue.nonEmpty && curTS > queue.front + period_millis) {
      queue.dequeue()
    }
  }

  def allowRequest(): Boolean = {
    this.synchronized {
      val curTS: Long = System.currentTimeMillis()
      dequeue(curTS)
      if(queue.size < request_limit) {
        queue.enqueue(curTS)
        true
      }
      else {
        false
      }
    }
  }

  def noOfRequestLeft(): Long = {
    request_limit - queue.size
  }

}
