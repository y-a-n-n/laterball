
#####################
# run the server
#####################

# Use this on a non-arm machine
# FROM openjdk:15

# Use this on an arm machine, such as a raspberry pi
FROM adoptopenjdk/openjdk11:latest

EXPOSE 8080
COPY build/distributions/laterball-server-2.4.3.tar .
WORKDIR /
RUN tar -xf laterball-server-2.4.3.tar && rm laterball-server-2.4.3.tar && ./laterball-server-2.4.3/bin/laterball-server
