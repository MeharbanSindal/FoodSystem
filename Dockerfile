# Use Tomcat with JDK 17
FROM tomcat:10.1-jdk17

# Remove default apps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR as ROOT (important)
COPY target/Food_Express_System.war /usr/local/tomcat/webapps/ROOT.war

# Set correct permissions (sometimes needed on Render)
RUN chmod -R 755 /usr/local/tomcat/webapps

# Expose port (Render uses 10000 internally but Tomcat still runs on 8080)
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]