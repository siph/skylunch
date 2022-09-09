#!/bin/sh

# Replace `key` with your api key provided by rapidapi
rapidApiKey=key
rapidApiRootUrl=https://airports-by-api-ninjas.p.rapidapi.com/v1/airports

# Replace `key` with your api key provided by google
googleApiKey=key
googleApiRootUrl=https://maps.googleapis.com/maps/api/place

# Redis
redisHost=localhost
redisPort=0
redisUsername=username
redisPassword=password

# Airport
export APPLICATION_AIRPORT_DAYSUNTILSTALE=0
export APPLICATION_AIRPORT_API_APIKEY=$rapidApiKey
export APPLICATION_AIRPORT_API_BASEURL=$rapidApiRootUrl

# Restaurant
export APPLICATION_RESTAURANT_DAYSUNTILSTALE=0
export APPLICATION_RESTAURANT_API_APIKEY=$googleApiKey
export APPLICATION_RESTAURANT_API_BASEURL=$googleApiRootUrl

# Redis
export SPRING_REDIS_HOST=$redisHost
export SPRING_REDIS_PORT=$redisPort
export SPRING_REDIS_USERNAME=$redisUsername
export SPRING_REDIS_PASSWORD=$redisPassword

# Server
export SERVER_PORT=8080
export LOGGING_LEVEL_ROOT=DEBUG

# Run
docker-compose up -d
