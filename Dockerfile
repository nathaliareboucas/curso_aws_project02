FROM openjdk:17-oracle
VOLUME /tmp
COPY ./target/aws_project02.jar /app/aws_project02.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "aws_project02.jar"]
EXPOSE 9090