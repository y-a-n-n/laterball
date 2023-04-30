version=$1
cd laterball
docker load -i laterball-server-$version.tar
docker load -i laterball-mongo-$version.tar
docker compose down
docker compose up -d