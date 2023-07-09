version=$1
env=$2
docker build -t laterball/laterball-server:$version --build-arg VERSION=$version -f Dockerfile .

docker tag laterball/laterball-server:$version laterball/laterball-server:latest

docker build -t laterball/mongo:$version -f Dockerfile-db .

docker tag laterball/mongo:$version laterball/mongo:latest
