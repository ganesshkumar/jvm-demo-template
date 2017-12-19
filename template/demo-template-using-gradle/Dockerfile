FROM openjdk:8-jdk-alpine as builder

USER root

RUN mkdir -p /root/src/

ADD function /root/src/function
WORKDIR /root/src/function
RUN ./gradlew build

ADD . /root/src/
WORKDIR /root/src
RUN ./gradlew clean shadow

FROM openjdk:8-jre-alpine

RUN apk --no-cache add curl \
    && echo "Pulling watchdog binary from Github." \
    && curl -sSL https://github.com/openfaas-incubator/of-watchdog/releases/download/0.1.0/of-watchdog > /usr/bin/fwatchdog \
    && chmod +x /usr/bin/fwatchdog \
    && apk del curl --no-cache

RUN addgroup -S runner && adduser -S -g runner runner
WORKDIR /home/runner
USER runner

COPY --chown=runner:runner --from=builder /root/src/build/libs/jvm-demo-template-1.0.0-all.jar /home/runner/

ENV fprocess="java -jar jvm-demo-template-1.0.0-all.jar"
ENV afterburn=true
ENV mode=afterburn

HEALTHCHECK --interval=1s CMD [ -e /tmp/.lock ] || exit 1
CMD ["fwatchdog"]
