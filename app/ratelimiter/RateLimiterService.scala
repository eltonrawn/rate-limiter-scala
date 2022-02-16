package ratelimiter

import com.typesafe.config.{Config, ConfigFactory}
import play.api.Logger
import ratelimiter.implementations.TokenBucket

import javax.inject.Singleton

case class RateLimiterConf(request_limit: Long, period_millis: Long)

@Singleton
class RateLimiterService {
  val logger: Logger = Logger(this.getClass())
  val config: Config = ConfigFactory.load()
  val rateLimiterMap = collection.mutable.Map[String, RateLimiterT]()
  val rateLimterDefaultConfig: RateLimiterConf = {
    val request_limit: Long = {
      if(config.hasPath("rate-limiter-default-config.request-limit")) {
        config.getLong("rate-limiter-default-config.request-limit")
      }
      else 50
    }
    val period_millis: Long = {
      if(config.hasPath("rate-limiter-default-config.period-millis")) {
        config.getLong("rate-limiter-default-config.period-millis")
      }
      else 10000
    }
    RateLimiterConf(request_limit, period_millis)
  }

  def getObject(key: String): RateLimiterT =  {
    if(rateLimiterMap.contains(key)) {
      return rateLimiterMap(key)
    }
    this.synchronized {
      if(!rateLimiterMap.contains(key)) {
        val rateLimiterConfig: RateLimiterConf = getRateLimiterConfig(key)
        logger.info("rateLimiterconfig: " + rateLimiterConfig.toString)
        rateLimiterMap(key) = new TokenBucket(rateLimiterConfig.request_limit, rateLimiterConfig.period_millis)
      }
      return rateLimiterMap(key)
    }
  }

  def getRateLimiterConfig(key: String): RateLimiterConf = {
    val exactKey = "rate-limiter-key-value-config.\"" + key + "\""
    if(config.hasPath(exactKey)) convertConfigToCase(config.getConfig(exactKey))
    else rateLimterDefaultConfig
  }

  def convertConfigToCase(config: Config): RateLimiterConf = {
    val request_limit: Long = {
      if(config.hasPath("request-limit")) config.getLong("request-limit")
      else rateLimterDefaultConfig.request_limit
    }
    val period_millis: Long = {
      if(config.hasPath("period-millis")) config.getLong("period-millis")
      else rateLimterDefaultConfig.period_millis
    }
    RateLimiterConf(request_limit, period_millis)
  }

}
