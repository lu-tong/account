FROM openjdk:8-jre-slim
MAINTAINER ufutao

ENV PARAMS=""

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY target/*.jar /app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /app.jar $PARAMS"]

EXPOSE 8080
COPY doc/config/application.yaml /root/configs/account/application.yaml