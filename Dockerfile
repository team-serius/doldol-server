FROM amd64/amazoncorretto:17

WORKDIR /app

COPY ./build/libs/Doldol-0.0.1-SNAPSHOT.jar /app/doldol-server.jar

EXPOSE 8080

CMD ["java", "-Duser.timezone=Asia/Seoul", "-jar", "doldol-server.jar"]