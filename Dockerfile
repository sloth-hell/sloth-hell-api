FROM eclipse-temurin:17-jdk AS base-image

RUN apt-get update && apt-get install -y git unzip

ARG GRADLE_VERSION=8.5
ARG KOTLIN_VERSION=1.9.20

RUN curl -LOs "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
    && unzip "gradle-${GRADLE_VERSION}-bin.zip" -d /opt \
    && rm "gradle-${GRADLE_VERSION}-bin.zip"

RUN curl -LOs "https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip" \
    && unzip "kotlin-compiler-${KOTLIN_VERSION}.zip" -d /opt \
    && rm "kotlin-compiler-${KOTLIN_VERSION}.zip"

ENV PATH="/opt/gradle-${GRADLE_VERSION}/bin:/opt/kotlinc/bin:${PATH}"

FROM base-image AS builder

ARG APP_NAME=sloth-hell
ARG REPOSITORY_NAME=${APP_NAME}-api
ARG DATE_FOR_DISABLE_CACHE
RUN echo ${DATE_FOR_DISABLE_CACHE}

WORKDIR /${REPOSITORY_NAME}
COPY . .

RUN gradle build --no-build-cache --no-daemon --stacktrace --warning-mode=all

FROM eclipse-temurin:17-jre-jammy

EXPOSE 8080

COPY --from=builder /sloth-hell-api/build/libs/sloth-hell.jar /app/sloth-hell.jar
WORKDIR /app

CMD [ "sh", "-c", "java -jar sloth-hell.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}" ]
