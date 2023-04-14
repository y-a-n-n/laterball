# TODO: https://blog.benoitblanchon.fr/github-action-run-ssh-commands/
host=$1
tag=$(git describe --tags --abbrev=0)
version="${tag#?}"
# build the tarball
./scripts/buildlocal.sh
# build the docker images
./scripts/builddocker.sh $version
# copy artefacts to the remote server
./scripts/deploy.sh $host
# run script on remote to docker-compose down and up
ssh $1 'bash -s' < ./scripts/run.sh $host