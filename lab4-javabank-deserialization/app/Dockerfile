FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /build
COPY pom.xml .
COPY src/ src/
RUN mvn -q -DskipTests package

FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /build/target/javabank.jar /app/javabank.jar
RUN mkdir -p /app/static \
    && echo "OSWE_LAB4{insecure_java_deserialization_rce}" > /flag.txt \
    && chmod 644 /flag.txt

EXPOSE 8080
CMD ["java", "-jar", "/app/javabank.jar"]
