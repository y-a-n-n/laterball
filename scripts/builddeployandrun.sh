# build the tarball
./scripts/buildlocal.sh
# build the docker images
./scripts/builddocker.sh
# copy artefacts to the remote server
./scripts/deploy.sh $1
# run script on remote to docker-compose down and up
ssh $1 'bash -s' < ./scripts/run.sh $1