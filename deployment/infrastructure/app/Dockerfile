FROM ${eclipse-temurin_11_alpine-digest}
USER ${eclipse-temurin_11_alpine-user}
COPY *.jar app.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Dcom.sun.management.jmxremote=false"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${profile} -jar app.jar" ]