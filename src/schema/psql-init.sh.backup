#! /bin/bash
CONTAINER_NAME=${CONTAINER_NAME:-"pg-docker"}
VOLUME_MOUNT=${VOLUME_MOUNT:-~/docker/volumes/postgres}
IMAGE_TAG="master"

if [ -n "$TEST" ]
then
    IMAGE_TAG="test"
fi

echo "Checking for running container '$CONTAINER_NAME'"
if [ -n "$(docker ps -f name=${CONTAINER_NAME} | grep -w $CONTAINER_NAME)" ]
then
    echo "A container with the name $CONTAINER_NAME is already running"
    exit 127
fi

echo "Config = {
  container: $CONTAINER_NAME
  persistent: $(if [ -z "$PERSIST_DATA" ]; then echo 'false'; else echo 'true\nvolume mount: $VOLUME_MOUNT'; fi)
  test: $(if [ -z "$TEST" ]; then echo 'false'; else echo 'true'; fi)
}"

if [ -z "$PERSIST_DATA" ]
then 
    docker run --rm --name $CONTAINER_NAME -d -p 5432:5432 reidit-pgsql:$IMAGE_TAG
else 
    docker run --rm --name $CONTAINER_NAME -d -p 5432:5432 -v $VOLUME_MOUNT:/var/lib/postgresql/data reidit-pgsql:$IMAGE_TAG
fi

echo "Let the database warm up ..."
sleep 10
echo "Done"