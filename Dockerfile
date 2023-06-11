FROM adoptopenjdk/openjdk8
COPY target/*.jar app.jar
CMD java -jar app.jar
