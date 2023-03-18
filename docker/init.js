db.log.insertOne({"message": "Database created."});
db.log.insertOne({"message": _getEnv("MONGO_USER")});
db.log.insertOne({"message": _getEnv("MONGO_USER")});

db.createUser(
    {
        user: _getEnv("MONGO_USER"),
        pwd: _getEnv("MONGO_PASSWORD"),
        roles: [
            "readWrite", "dbAdmin"
        ]
    }
);