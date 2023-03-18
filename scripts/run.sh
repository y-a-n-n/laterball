cd laterball
docker load -i laterball-server-latest.tar
docker load -i laterball-mongo-latest.tar
docker compose down
docker compose up -d