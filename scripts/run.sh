version=$1
env=$2
cd /usr/bin/laterball
docker load -i laterball-server-$version.tar
docker load -i laterball-mongo-$version.tar
docker compose -f docker-compose.$env.yml down
docker compose -f docker-compose.$env.yml up -d