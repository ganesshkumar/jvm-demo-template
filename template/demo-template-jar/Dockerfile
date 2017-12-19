FROM openjdk:8-jdk-alpine as builder

USER root

RUN mkdir -p /root/src/

ADD function /root/src/function
WORKDIR /root/src/function
RUN ./gradlew build

ADD Main.java /root/src/
ADD libs /root/src/libs
WORKDIR /root/src
RUN javac -cp .:libs/afterburn-java-parser-1.0.0-rc1.jar:function/build/libs/java8-gradle-1.0.jar Main.java

FROM openjdk:8-jre-alpine

RUN apk --no-cache add curl \
    && echo "Pulling watchdog binary from Github." \
    && curl -sSL https://github.com/openfaas-incubator/of-watchdog/releases/download/0.1.0/of-watchdog > /usr/bin/fwatchdog \
    && chmod +x /usr/bin/fwatchdog \
    && apk del curl --no-cache

RUN addgroup -S runner && adduser -S -g runner runner
WORKDIR /home/runner
USER runner

COPY --from=builder /root/src/function/build/libs /home/runner/libs
COPY --from=builder /root/src/libs /home/runner/libs
COPY --from=builder /root/src/Main.class /home/runner

ENV fprocess="java -cp .:libs/afterburn-java-parser-1.0.0-rc1.jar:function/build/libs/java8-gradle-1.0.jar Main"
ENV afterburn=true
ENV mode=afterburn

HEALTHCHECK --interval=1s CMD [ -e /tmp/.lock ] || exit 1
CMD ["fwatchdog"]
