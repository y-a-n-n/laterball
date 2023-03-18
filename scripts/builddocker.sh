tag=$(git describe --tags --abbrev=0)
version="${tag#?}"
docker build -t laterball/laterball-server --build-arg VERSION=$version \
--build-arg KTOR_ENV=$KTOR_ENV \
--build-arg RAPID_API_KEY=$RAPID_API_KEY \
--build-arg MONGO_USER=$MONGO_USER \
--build-arg MONGO_PASSWORD=$MONGO_PASSWORD \
--build-arg MONGO_HOST=$MONGO_HOST \
--build-arg LATERBALL_TWITTER_API_KEY=$LATERBALL_TWITTER_API_KEY \
--build-arg LATERBALL_TWITTER_API_SECRET=$LATERBALL_TWITTER_API_SECRET \
--build-arg LATERBALL_ACCESS_TOKEN=$LATERBALL_ACCESS_TOKEN \
--build-arg LATERBALL_ACCESS_SECRET=$LATERBALL_ACCESS_SECRET -f Dockerfile .
.

docker build -t laterball/mongo --build-arg MONGO_USER=$MONGO_USER --build-arg MONGO_PASSWORD=$MONGO_PASSWORD -f Dockerfile-db .