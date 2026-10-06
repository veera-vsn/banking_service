# ---------- Stage 1: build the WAR ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
# copy pom.xml first so the dependency download is cached until pom.xml changes
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ---------- Stage 2: runtime image (no Maven, no source code) ----------
FROM tomcat:10.1-jdk17

# remove sample apps, create a non-root user, give it the folders Tomcat writes to
RUN rm -rf /usr/local/tomcat/webapps/* /usr/local/tomcat/webapps.dist \
 && groupadd -r app && useradd -r -g app app \
 && chown -R app:app /usr/local/tomcat/logs /usr/local/tomcat/temp \
                     /usr/local/tomcat/work /usr/local/tomcat/webapps

# ROOT.war = the app is served at /
COPY --from=build --chown=app:app /build/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

USER app
EXPOSE 8080

# no curl in this image, so use bash's built-in /dev/tcp to call /health (200 = healthy)
HEALTHCHECK --interval=15s --timeout=3s --start-period=40s --retries=3 \
  CMD ["bash", "-c", "exec 3<>/dev/tcp/127.0.0.1/8080 && printf 'GET /health HTTP/1.0\\r\\n\\r\\n' >&3 && head -n1 <&3 | grep -q ' 200 '"]
