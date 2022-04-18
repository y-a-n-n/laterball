#!/bin/bash

echo "Update system..."
sudo apt-get update && sudo apt-get upgrade
echo "Get docker"
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
# todo: promt the user for input
sudo usermod -aG docker pi

echo "Get docker-compose"
sudo apt-get install libffi-dev libssl-dev
sudo apt install python3-dev
sudo apt-get install -y python3 python3-pip
sudo pip3 install docker-compose


# todo: prompt the user to restart system

# Set this service to auto-run at reboot
# sudo systemctl enable docker
