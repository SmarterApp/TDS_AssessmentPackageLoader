FROM frolvlad/alpine-oraclejdk8:slim

VOLUME /tmp
ADD docker-startup.sh /
ADD tds-aspl-*.jar tds-apl.jar
RUN sh -c 'touch /docker-startup.sh && touch /tds-apl.jar'
RUN apk --no-cache add curl

ENTRYPOINT ["/docker-startup.sh"]