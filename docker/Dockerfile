# 1단계: 빌드
FROM gradle:8.14.3-jdk17 AS build
WORKDIR /apps
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# 2단계: 실행
FROM openjdk:17-jdk-slim
WORKDIR /apps
COPY --from=build /apps/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
