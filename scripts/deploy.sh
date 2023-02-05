# Save local docker image to tar
mkdir -p ./build/docker
docker save laterball/laterball-server > ./build/docker/laterball-latest.tar

# SCP tar to remote
scp ./docker-compose.yml ./build/docker/laterball-latest.tar $1:~/laterball

# Run script on remote to docker-compose down and upChange bitbucket auth strategy
ssh $1 'bash -s' < ./scripts/run.sh