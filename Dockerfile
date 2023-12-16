FROM eclipse-temurin:17-jdk AS base-image

RUN apt-get update && apt-get install -y git unzip
RUN git config --global user.name "lemphis"
RUN git config --global user.email "lemphis@gmail.com"

ARG GRADLE_VERSION=8.5
ARG KOTLIN_VERSION=1.9.20

RUN curl -LO "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
RUN unzip "gradle-${GRADLE_VERSION}-bin.zip" -d /opt
RUN rm "gradle-${GRADLE_VERSION}-bin.zip"
ENV PATH="/opt/gradle-${GRADLE_VERSION}/bin:${PATH}"

RUN curl -LO "https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip"
RUN unzip "kotlin-compiler-${KOTLIN_VERSION}.zip" -d /opt
RUN rm "kotlin-compiler-${KOTLIN_VERSION}.zip"
ENV PATH="/opt/kotlinc/bin:${PATH}"

FROM base-image AS builder

ARG APP_NAME=sloth-hell
ARG REPOSITORY_NAME=${APP_NAME}-api
ARG DATE_FOR_DISABLE_CACHE
RUN echo ${DATE_FOR_DISABLE_CACHE}

RUN git clone https://github.com/sloth-hell/${REPOSITORY_NAME}.git
WORKDIR /${REPOSITORY_NAME}
RUN gradle build --no-build-cache --no-daemon --stacktrace --warning-mode=all

FROM eclipse-temurin:17-jre-jammy

EXPOSE 8080

ARG SPRING_PROFILES_ACTIVE
ARG APP_NAME=sloth-hell
ARG REPOSITORY_NAME=${APP_NAME}-api
COPY --from=builder /${REPOSITORY_NAME}/build/libs/${APP_NAME}.jar /app/${APP_NAME}.jar
WORKDIR /app

ENTRYPOINT [ "java", "-jar", "sloth-hell.jar" ]
CMD [ "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}" ]
