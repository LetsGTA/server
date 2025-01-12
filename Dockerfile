FROM eclipse-temurin:21-jdk AS build

WORKDIR /app
COPY . .

# Gradle Wrapper 및 빌드 실행
RUN ./gradlew clean build -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

