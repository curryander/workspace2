FROM openjdk:11-jre-slim

RUN mkdir /app
COPY libs /app/libs
COPY ${project.artifactId}.jar /app

CMD ["java", "-jar", "/app/${project.artifactId}.jar"]