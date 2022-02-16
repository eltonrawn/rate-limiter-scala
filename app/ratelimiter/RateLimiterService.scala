package ratelimiter

import com.typesafe.config.{Config, ConfigFactory}
import play.api.Logger
import ratelimiter.implementations.TokenBucket

import javax.inject.Singleton
import scala.collection.mutable

case class RateLimiterConf(request_limit: Long, period_millis: Long, algorithm: String)

@Singleton
class RateLimiterService {
  val logger: Logger = Logger(this.getClass())
  val config: Config = ConfigFactory.load()
  val rateLimiterMap: mutable.Map[String, RateLimiterT] = collection.mutable.Map[String, RateLimiterT]()

  val rateLimterDefaultConfig: RateLimiterConf = {
    val request_limit: Long = configCheckAndGetLong("rate-limiter-default-config.request-limit", 50)
    val period_millis: Long = configCheckAndGetLong("rate-limiter-default-config.period-millis", 10000)
    val algorithm: String = configCheckAndGetString("rate-limiter-default-config.algorithm", RateLimiterAlgorithms.tokenBucket)
    RateLimiterConf(request_limit, period_millis, algorithm)
  }

  def getObject(key: String): RateLimiterT =  {
    if(rateLimiterMap.contains(key)) {
      return rateLimiterMap(key)
    }
    this.synchronized {
      if(!rateLimiterMap.contains(key)) {
        val rateLimiterConfig: RateLimiterConf = getRateLimiterConfig(key)
        logger.info("rateLimiterconfig: " + rateLimiterConfig.toString)
        rateLimiterMap(key) = rateLimiterConfig.algorithm match {
          case RateLimiterAlgorithms.tokenBucket => new TokenBucket(rateLimiterConfig.request_limit, rateLimiterConfig.period_millis)
          case _ => new TokenBucket(rateLimiterConfig.request_limit, rateLimiterConfig.period_millis)
        }
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
    val request_limit: Long = configCheckAndGetLong("request-limit", rateLimterDefaultConfig.request_limit)
    val period_millis: Long = configCheckAndGetLong("period-millis", rateLimterDefaultConfig.period_millis)
    val algorithm: String = configCheckAndGetString("algorithm", rateLimterDefaultConfig.algorithm)
    RateLimiterConf(request_limit, period_millis, algorithm)
  }

  def configCheckAndGetString(key: String, defaultVal: String): String = {
    if(config.hasPath(key)) config.getString(key)
    else defaultVal
  }

  def configCheckAndGetLong(key: String, defaultVal: Long): Long = {
    if(config.hasPath(key)) config.getLong(key)
    else defaultVal
  }

}
