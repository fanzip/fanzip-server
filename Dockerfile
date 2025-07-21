# Tomcat 9 with OpenJDK 17 사용
FROM tomcat:9.0-jdk17

# WAR 파일을 Tomcat webapps 디렉토리로 복사
ARG WAR_FILE=build/libs/*.war
COPY ${WAR_FILE} /usr/local/tomcat/webapps/fanzip.war

EXPOSE 8080

# Tomcat 시작
CMD ["catalina.sh", "run"]