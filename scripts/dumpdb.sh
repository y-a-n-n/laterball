mongoContainerID=$1
mkdir -p ./resources/test/dbSnapshot
docker exec -i $mongoContainerID mongoexport -d laterball -c fixtures -u $MONGO_INITDB_ROOT_USERNAME -p $MONGO_INITDB_ROOT_PASSWORD --authenticationDatabase admin --jsonArray > ./resources/test/dbSnapshot/fixtures.json
docker exec -i $mongoContainerID mongoexport -d laterball -c stats -u $MONGO_INITDB_ROOT_USERNAME -p $MONGO_INITDB_ROOT_PASSWORD --authenticationDatabase admin --jsonArray > ./resources/test/dbSnapshot/stats.json
docker exec -i $mongoContainerID mongoexport -d laterball -c odds -u $MONGO_INITDB_ROOT_USERNAME -p $MONGO_INITDB_ROOT_PASSWORD --authenticationDatabase admin --jsonArray > ./resources/test/dbSnapshot/odds.json
docker exec -i $mongoContainerID mongoexport -d laterball -c lastUpdated -u $MONGO_INITDB_ROOT_USERNAME -p $MONGO_INITDB_ROOT_PASSWORD --authenticationDatabase admin --jsonArray > ./resources/test/dbSnapshot/lastUpdated.json
docker exec -i $mongoContainerID mongoexport -d laterball -c nextUpdated -u $MONGO_INITDB_ROOT_USERNAME -p $MONGO_INITDB_ROOT_PASSWORD --authenticationDatabase admin --jsonArray > ./resources/test/dbSnapshot/nextUpdated.json