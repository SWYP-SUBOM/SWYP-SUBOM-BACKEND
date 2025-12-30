# 1단계: 빌드
FROM gradle:8.14.3-jdk17 AS build
WORKDIR /apps
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# 2단계: 실행
FROM eclipse-temurin:17-jdk-jammy
ENV TZ=Asia/Seoul
RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
WORKDIR /apps
RUN mkdir -p /apps/logs \
    && mkdir -p /apps/logs/archived \
    && chmod -R 777 /apps/logs

COPY --from=build /apps/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]

