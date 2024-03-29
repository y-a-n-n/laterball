host=$1
version=$2
env=$3
# Save local docker image to tar
mkdir -p ./build/docker
docker save laterball/laterball-server > ./build/docker/laterball-server-$version.tar
docker save laterball/mongo > ./build/docker/laterball-mongo-$version.tar

ssh $host "sudo mkdir -p /usr/bin/laterball"

# SCP tar to remote
scp ./docker-compose.$env.yml .env.$env ./nginx/laterball.$env.conf ./build/docker/laterball-server-$version.tar ./build/docker/laterball-mongo-$version.tar $host:/usr/bin/laterball