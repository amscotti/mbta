FROM clojure:tools-deps AS builder

COPY . /usr/src/app
WORKDIR /usr/src/app

RUN clj -T:build uber

FROM ghcr.io/graalvm/native-image:22.2.0 AS compiler

WORKDIR /

COPY --from=builder /usr/src/app/target/mbta-standalone.jar mbta-standalone.jar
RUN native-image -jar mbta-standalone.jar --initialize-at-build-time --no-fallback

ENTRYPOINT [ "/mbta-standalone" ]
