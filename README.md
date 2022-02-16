Rate Limiter Scala
------------------
Rate limiter implemented with scala.

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

any pattern supported by play framework can be place in "conf/routes" folder but uri in "rate-limiter-key-value-config" only supports regular expression for now. Regex details can be found here -> https://www.playframework.com/documentation/2.8.x/ScalaRouting#Dynamic-parts-with-custom-regular-expressions

3 values can be set in config ->

    "request-limit" -> No of request that can be served in a period
    "period-millis" -> Length of the period in millisecond
    "algorithm" -> Rate limiting algorithm

Two implementations exist for now ->

    "token-bucket"
    "sliding-logs"


Local Deployment
----------
There should be "public/hoteldb.csv" file present with format ->
    
    CITY,HOTELID,ROOM,PRICE
    XX,XX,XX,XX
    XX,XX,XX,XX

Component version:

    java -> 1.8
    scala -> 2.13.8
    sbt -> 1.5.2

To start the server, run:

    sbt run

To trigger functional tests, run:

    sbt test
