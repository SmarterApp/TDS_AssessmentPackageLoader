FROM frolvlad/alpine-oraclejdk8:slim

VOLUME /tmp
ADD docker-startup.sh /
ADD tds-support-tool-external-*.jar tds-support-tool-external.jar
RUN sh -c 'touch /docker-startup.sh && touch /tds-support-tool-external.jar'
RUN apk --no-cache add curl

ENTRYPOINT ["/docker-startup.sh"]