FROM adoptopenjdk/openjdk11:latest

ARG VERSION

ENV VERSION=${VERSION}

EXPOSE 8080
COPY build/distributions/laterball-server-$VERSION.tar .
WORKDIR /
RUN tar -xf laterball-server-$VERSION.tar && rm laterball-server-$VERSION.tar
ENTRYPOINT ["/bin/bash", "-c", "/laterball-server-$VERSION/bin/laterball-server"]
#CMD ["/laterball-server-$VERSION/bin/laterball-server"]