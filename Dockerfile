FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/montecarlo.jar /montecarlo/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/montecarlo/app.jar"]
