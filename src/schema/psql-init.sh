#! /bin/bash
CONTAINER_NAME=${CONTAINER_NAME:-"pg-docker"}
VOLUME_MOUNT=${VOLUME_MOUNT:-~/docker/volumes/postgres}
POSTGRES_USER=${POSTGRES_USER:-reidit_api}
POSTGRES_PASSWORD=${POSTGRES_PASSWORD:-youshouldusearealpassword}
POSTGRES_DB=${POSTGRES_DB:-reidit}

echo "Checking for running container '$CONTAINER_NAME'"
if [[ -n $(docker ps -f name=${CONTAINER_NAME} | grep -w $CONTAINER_NAME) ]] 
then
    echo "A container with the name $CONTAINER_NAME is already running"
    exit 127
fi

echo "Config = {
  container: $CONTAINER_NAME
  persistent: $(if [ -z $PERSIST_DATA ]; then echo 'false'; else echo 'true\nvolume mount: $VOLUME_MOUNT'; fi)
  user: $POSTGRES_USER
  db: $POSTGRES_DB
}"

echo "Container creation will proceed in 3 seconds (Ctrl-C to cancel)"

sleep 3

if [ -z $PERSIST_DATA ]
then 
    docker run --rm --name $CONTAINER_NAME -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD -e POSTGRES_USER=$POSTGRES_USER -e POSTGRES_DB=$POSTGRES_DB -d -p 5432:5432 postgres
else 
    docker run --rm --name $CONTAINER_NAME -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD -e POSTGRES_USER=$POSTGRES_USER -e POSTGRES_DB=$POSTGRES_DB -d -p 5432:5432 -v $VOLUME_MOUNT:/var/lib/postgresql/data postgres
fi