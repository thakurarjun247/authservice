FROM eclipse-temurin:24-jdk AS builder

RUN apt-get update && apt-get install -y curl tar --no-install-recommends && rm -rf /var/lib/apt/lists/*
ARG MAVEN_VERSION=3.9.6
ARG BASE_URL=https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries
RUN curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    tar -xzf /tmp/apache-maven.tar.gz -C /opt && \
    rm /tmp/apache-maven.tar.gz
ENV PATH="/opt/apache-maven-${MAVEN_VERSION}/bin:${PATH}"

WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY mvnw .
COPY .mvn ./.mvn
RUN chmod +x mvnw
RUN ./mvnw clean package -Dmaven.test.skip=true

FROM eclipse-temurin:24-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/authservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]