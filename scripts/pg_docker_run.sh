#!/usr/bin/env bash

DOCKER_ID=$(docker run -d --name pg -p 5432:5432 postgres)
docker exec $DOCKER_ID postgres -D /var/lib/postgresql/data
sleep 10
docker exec $DOCKER_ID createdb -U postgres -O postgres tasksDb
