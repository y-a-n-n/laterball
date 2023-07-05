# TODO: https://blog.benoitblanchon.fr/github-action-run-ssh-commands/
host=$1
env=$2
tag=$(git describe --tags --abbrev=0)
version="${tag#?}"
echo "Deploying $version to $host"
# build the tarball
./scripts/buildlocal.sh
# build the docker images
./scripts/builddocker.sh $version $env
# copy artefacts to the remote server
./scripts/deploy.sh $host $version $env
# run script on remote to docker-compose down and up
ssh $host 'bash -s' < ./scripts/run.sh $version $env