FROM frolvlad/alpine-oraclejdk8:slim

VOLUME /tmp
ADD docker-startup.sh /
ADD tds-support-tool-*.jar tds-support-tool.jar
RUN sh -c 'touch /docker-startup.sh && touch /tds-support-tool.jar'
RUN apk --no-cache add curl

ENTRYPOINT ["/docker-startup.sh"]