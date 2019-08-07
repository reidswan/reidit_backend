#! /bin/bash
CONTAINER_NAME=${CONTAINER_NAME:-"pg-docker"}
VOLUME_MOUNT=${VOLUME_MOUNT:-~/docker/volumes/postgres}
IMAGE_TAG="master"
SCRIPT=$(readlink -f "$0")
SCRIPTPATH=$(dirname "$SCRIPT")
PGPASSFILE=${PGPASSFILE:-$SCRIPTPATH/.pgpass}
PORT=5432

if [ -n "$TEST" ]
then
    IMAGE_TAG="test"
    PORT=9000
fi

echo "Checking for running container '$CONTAINER_NAME'"
if [ -n "$(docker ps -af name=${CONTAINER_NAME} | grep -w $CONTAINER_NAME)" ]
then
    echo "A container with the name $CONTAINER_NAME is already running; try executing"
    echo "      docker rm $CONTAINER_NAME"
    exit 127
fi

echo "Config = {
  container: $CONTAINER_NAME
  persistent: $(if [ -z "$PERSIST_DATA" ]; then echo 'false'; else echo 'true\nvolume mount: $VOLUME_MOUNT'; fi)
  test: $(if [ -z "$TEST" ]; then echo 'false'; else echo 'true'; fi)
}"

if [ -z "$PERSIST_DATA" ]
then 
    docker run --rm --name $CONTAINER_NAME -d -p $PORT:5432 reidit-pgsql:$IMAGE_TAG
else 
    docker run --rm --name $CONTAINER_NAME -d -p $PORT:5432 -v $VOLUME_MOUNT:/var/lib/postgresql/data reidit-pgsql:$IMAGE_TAG
fi

MAX_RETRIES=50
WAIT_TIME=1
RETRIES=0

echo "Waiting up to $MAX_RETRIES seconds for the database to be ready"

while ! psql -h localhost -p $PORT -U reidit_api -d reidit -w -c "SELECT 1" > /dev/null 2>&1
do
    RETRIES=$(( $RETRIES + 1 ))
    if [ $RETRIES -gt $MAX_RETRIES ]
    then 
        echo "Max retries exceeded"
        exit 1
    fi
    sleep $WAIT_TIME
done

