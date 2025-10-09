FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*-jar-with-dependencies.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --spider -q http://localhost:8080/health || exit 1
ENTRYPOINT ["java","-jar","app.jar"]
