db.log.insertOne({"message": "Database created."});
db.log.insertOne({"message": _getEnv("MONGO_INITDB_ROOT_USERNAME")});
db.log.insertOne({"message": _getEnv("MONGO_INITDB_ROOT_USERNAME")});

db.createUser(
    {
        user: _getEnv("MONGO_INITDB_ROOT_USERNAME"),
        pwd: _getEnv("MONGO_INITDB_ROOT_PASSWORD"),
        roles: [
            "readWrite", "dbAdmin"
        ]
    }
);