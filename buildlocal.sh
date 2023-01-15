./gradlew assembleDist
tag=$(git describe --tags --abbrev=0)
version="${tag#?}"
docker build -t laterball/laterball-server --build-arg VERSION=$version .