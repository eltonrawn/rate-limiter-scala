Rate Limiter Scala
------------------
Rate limiter implemented with scala and play framework.

Config
------
config can be found in "conf/application.conf" with format ->

```
"rate-limiter-key-value-config" = {
    "url1" = {
        "request-limit" = XX
        "period-millis" = XX
    }
    "url2" = {
        "request-limit" = XX
        "period-millis" = XX
        "algorithm" = "XX"
    }
}

"rate-limiter-default-config" = {
    "request-limit" = XX
    "period-millis" = XX
    "algorithm" = "XX"
}
```
"rate-limiter-key-value-config" contains config for separate uri.

Config falls back to "rate-limiter-default-config" if any uri doesn't exist in "rate-limiter-key-value-config"

Any uri pattern supported by play framework can be placed in "conf/routes" folder but uri in 
"rate-limiter-key-value-config" only supports regular expression for now. Regex details can be found here -> 
https://www.playframework.com/documentation/2.8.x/ScalaRouting#Dynamic-parts-with-custom-regular-expressions

3 values can be set in config ->

    "request-limit" -> No of request that can be served in a period
    "period-millis" -> Length of the period in millisecond
    "algorithm" -> Rate limiting algorithm

Two rate-limiting algorithm implementations exist for now ->

    "token-bucket"
    "sliding-logs"

Development
-----------
Decorator pattern is used to provide rate-limiting functionality where any action returned by controller can be 
wrapped by "ratelimiter.RateLimiterAction". Example can be found in "hotel.HotelController" 

"ratelimiter.RateLimiterService" provides all the book-keeping for singleton-instances of every unique 
"ratelimiter.RateLimiterAction". "Factory" and "Singleton" pattern is used to implement this.

Rate-limiter algorithm implementations can be found under "ratelimiter.implementations.*" 

Files related to route, log and configuration are placed under "conf/"

All ratelimiter files reside in "ratelimiter" package

All api related files reside in "hotel" package
 
    routes
    /city/:id
    /room/:id
    routes also support optional query parameters
    /city/:id?asc=true
    /room/:id?asc=false

There should be "public/hoteldb.csv" file present. Failed to do so will generate error (Intentional). Format ->

    CITY,HOTELID,ROOM,PRICE
    XX,XX,XX,XX
    XX,XX,XX,XX

Ide run/debug setup for intellij (for personal documentation)
    
    go to edit configurations
    add new configuration -> sbt task
    give a name and write "run" in text-box under tasks
    click apply and ok (till this would allow us to run app through ide)
    go to settings->build,execution,deployment->build tools->sbt
    tick project reload and builds under sbt shell
    tick enable debugging (till this would allow us to debug app through ide)


Local Deployment
----------

Components should be installed with version:

    java -> 1.8
    scala -> 2.13.8
    sbt -> 1.5.2

To start the server, run:

    sbt run

To trigger functional tests, run:

    sbt test

Production Deployment
---------------------
https://www.playframework.com/documentation/2.8.x/Deploying

Future work:
-----------
1. Provide library for easy integration. (providing functionality with annotation would have been convenient)
2. Routes in "conf/application.conf" only support regex. Provide flexible route support in config.
3. Add more rate-limiting algorithm implementations.
4. Only integration tests given for now. Add unit tests to increase test-coverage.