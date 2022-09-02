FROM adoptopenjdk/openjdk11:alpine AS builder
ENV HOME=/home/skylunch/app/
WORKDIR $HOME
COPY . .
RUN ./mvnw clean install -DskipTests

FROM adoptopenjdk/openjdk11:alpine
COPY --from=builder /home/skylunch/app/target/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
