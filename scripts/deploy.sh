# Save local docker image to tar
mkdir -p ./build/docker
docker save laterball/laterball-server > ./build/docker/laterball-server-latest.tar
docker save laterball/mongo > ./build/docker/laterball-mongo-latest.tar

# SCP tar to remote
scp ./docker-compose.yml ./build/docker/laterball-server-latest.tar ./build/docker/laterball-mongo-latest.tar $1:~/laterball