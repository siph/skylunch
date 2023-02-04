![tests workflow](https://github.com/siph/skylunch/actions/workflows/service-tests.yaml/badge.svg)
![image workflow](https://github.com/siph/skylunch/actions/workflows/build-image.yaml/badge.svg)
# Skylunch

The $100 hamburger is a general aviation tradition of flying to a neighboring airport for lunch. The meal is not what is important, it's only an excuse to fly. But that doesn't mean that you should have to settle for a mediocre meal.

`Skylunch` is a simple, non-blocking api built using [Spring Webflux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html) that, provided with an airport code, will return a list of restaurants with their ratings and other details.

The purpose of `Skylunch` is to help aviators find new and interesting destinations to elevate the enjoyment of their favorite hobby.

## How it works

### Technologies

`Skylunch` is built using the following technologies:
 - [Kotlin](https://github.com/JetBrains/kotlin)
 - [Spring Webflux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
 - [Redis OM Spring](https://github.com/redis/redis-om-spring)
 - [Spring for GraphQL](https://spring.io/projects/spring-graphql#overview)

### Data Storage and Access

![diagram](./doc/diagram.png)

Data is stored with Redis using [Redis OM Spring](https://github.com/redis/redis-om-spring).

Upon receiving a request, `Skylunch` will search the Redis cache/db for a result before querying the external api. External api calls will then be cached in Redis for subsequent requests.

Geographical searches are performed using the built-in functions of Redis OM:

```kotlin
val restaurants = restaurantRepository.findByLocationNear(
    location,
    Distance(restaurantProperties.radius.toDouble(), Metrics.METERS),
)
```

Local results will also be checked for 'staleness' according to the configuration properties. Results that are older than the threshold age will be re-queried by external api.

### Note About Blocking Calls in Redis OM Java

As of version `0.5.1`, a fully non-blocking application is not possible using the Redis OM. This is because Redis OM uses the [Jedis](https://github.com/redis/jedis) library instead of the non-blocking [Lettuce](https://github.com/lettuce-io/lettuce-core) library to access Redis. Therefore, calls to the repository will be blocking.

## Try It Out

### Graphql

The application exposes the `/graphiql` endpoint which can be used to test the application and explore the schema.
Queries are then made via `/graphql`.

### Http

A typical http endpoint is also provided and can be used at `/api/v1/search?code=<ICAO or IATA>`.
Example: `/api/v1/search?code=lax`.

## Deployment

The infrastructure and deployment are covered in their own [Readme](ci/README.md).
