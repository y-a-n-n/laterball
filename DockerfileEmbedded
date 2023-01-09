#####################
# build the jar
#####################

FROM gradle:jdk15 as builder
COPY --chown=gradle:gradle application /application
WORKDIR /application
RUN gradle clean build jar

#####################
# run the server
#####################

# Use this on a non-arm machine
# FROM openjdk:15

# Use this on an arm machine, such as a raspberry pi
FROM arm32v7/adoptopenjdk:15

EXPOSE 8080
COPY --from=builder /application/build/libs/miniktor-0.0.1.jar .
WORKDIR /
CMD java -jar ./miniktor-0.0.1.jar
 
