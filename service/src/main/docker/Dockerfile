FROM frolvlad/alpine-oraclejdk8:slim

VOLUME /tmp
ADD docker-startup.sh /
ADD tds-support-tool-*.jar tds-support-tool.jar
RUN sh -c 'touch /docker-startup.sh && touch /tds-support-tool.jar'
RUN apk --no-cache add curl

#Embed SAML security files from build machine file-system
RUN mkdir -p /opt/sbtds/resources
ADD security /opt/sbtds/resources/security
ENV SUPPORTTOOL_SECURITY_DIR file:///opt/sbtds/resources/security

ENTRYPOINT ["/docker-startup.sh"]