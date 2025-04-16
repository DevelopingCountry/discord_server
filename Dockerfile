FROM openjdk:17

WORKDIR /app

COPY build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
