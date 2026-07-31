FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /build
COPY pom.xml .
COPY src/ src/
COPY static/ static/
RUN mvn -q -DskipTests package

FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /build/target/campusnet.jar /app/campusnet.jar
COPY --from=build /build/static /app/static

RUN echo "OSWE_LAB8{sqli_massassign_xxe_lfi_chain_$(date +%s)}" > /flag.txt \
    && chmod 644 /flag.txt

EXPOSE 8080
CMD ["java", "-jar", "/app/campusnet.jar"]
