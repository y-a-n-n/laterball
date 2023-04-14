host=$1
version=$2
# Save local docker image to tar
mkdir -p ./build/docker
docker save laterball/laterball-server > ./build/docker/laterball-server-$version.tar
docker save laterball/mongo > ./build/docker/laterball-mongo-$version.tar

# SCP tar to remote
scp ./docker-compose.yml ./build/docker/laterball-server-latest.tar ./build/docker/laterball-mongo-latest.tar $host:~/laterball