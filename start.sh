#!/bin/sh

# Replace `key` with your api key provided by rapidapi
rapidApiKey=key
rapidApiRootUrl=https://airports-by-api-ninjas.p.rapidapi.com/v1/airports

# Replace `key` with your api key provided by google
googleApiKey=key
googleApiRootUrl=https://maps.googleapis.com/maps/api/place

# Airport
export APPLICATION_AIRPORT_DAYSUNTILSTALE=0
export APPLICATION_AIRPORT_API_APIKEY=$rapidApiKey
export APPLICATION_AIRPORT_API_BASEURL=$rapidApiRootUrl

# Restaurant
export APPLICATION_RESTAURANT_DAYSUNTILSTALE=0
export APPLICATION_RESTAURANT_API_APIKEY=$googleApiKey
export APPLICATION_RESTAURANT_API_BASEURL=$googleApiRootUrl

# Redis
export APPLICATION_REDIS_HOST=localhost
export APPLICATION_REDIS_PORT=6379

# Server
export SERVER_PORT=8080
export LOGGING_LEVEL_ROOT=INFO

# Run
docker-compose up -d
