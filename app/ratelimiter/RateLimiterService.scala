package ratelimiter

import com.typesafe.config.{Config, ConfigFactory}
import ratelimiter.implementations.TokenBucket

import javax.inject.Singleton

@Singleton
class RateLimiterService {
  val config: Config = ConfigFactory.load()
  var rateLimiterMap = collection.mutable.Map[String, RateLimiterT]()

  def getObject(key: String): RateLimiterT =  {
    if(rateLimiterMap.contains(key)) {
      return rateLimiterMap(key)
    }
    this.synchronized {
      if(!rateLimiterMap.contains(key)) {
        val rateLimiterConfig: Config = getRateLimiterConfig(key)
        rateLimiterMap(key) = new TokenBucket(rateLimiterConfig.getLong("request-limit"),
          rateLimiterConfig.getLong("period-millis"))
      }
      return rateLimiterMap(key)
    }
  }

  def getRateLimiterConfig(key: String): Config = {
    val exactKey = "rate-limiter-key-value-config.\"" + key + "\""
    if(config.hasPath(exactKey)) {
      config.getConfig(exactKey)
    }
    else {
      config.getConfig("rate-limiter-default-config")
    }
  }

}
