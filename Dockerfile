FROM adoptopenjdk/openjdk8:latest

ARG VERSION

EXPOSE 8080
COPY build/distributions/laterball-server-2.4.3.tar .
WORKDIR /
RUN tar -xf laterball-server-$VERSION.tar && rm laterball-server-$VERSION.tar
CMD ["/laterball-server-2.4.3/bin/laterball-server"]