#FROM icr.io/appcafe/open-liberty:full-java11-openj9-ubi
FROM icr.io/appcafe/open-liberty:full-java11-openj9-ubi

ARG VERSION=1.0
ARG REVISION=SNAPSHOT

# Default setting for the verbose option
ARG VERBOSE=true
# Optional functionality
ARG TLS=true

#ENV KEYSTORE_REQUIRED "false"
# trust certificates from well known CA's (also set in server.xml)
#ENV SEC_TLS_TRUSTDEFAULTCERTS=true
# trust certificates from within the cluster, such as Red Hat SSO.
ENV SEC_IMPORT_K8S_CERTS=true

ENV DB_HOSTNAME "192.168.178.24"

# copy config
COPY --chown=1001:0 src/main/liberty/config/ /config/
# copy apps
COPY --chown=1001:0 target/*.war /config/apps/


#only for slim images
#RUN features.sh


#RUN mkdir /app
#COPY libs /app/libs
#COPY ${project.artifactId}.jar /app
#
#CMD ["java", "-jar", "/app/${project.artifactId}.jar"]

RUN configure.sh
