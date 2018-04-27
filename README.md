# TDS Support Tool
## Overview
`TDS_SupportTool` provides a web interface for various support tools, with features such as assessment loading, test-specification 
package validation, test-specification package conversion, and 

The `TDS_SupportTool` consists of various modules:

* **client:** Contains the POJOs/classes needed for a consumer to interact with the Assessment Package Loader
* **service:** Contains the spring application code and the angular web application
* **loader** Contains the loader application business logic


## Setup
The following tools will be needed to build the project:

1. Maven
2. Java 8

The following external dependencies are required to run the support tool:

1. A mongodb instance with a `support-tool` database and authorized user
    - The database is used primarily to manage and track the state of the test package loading process, as well as to persist the status of loaded test packages
2. A rabbitmq server used to manage job execution and communication
3. An S3 repository
    a. Item content metadata is fetched from this repository when loading into THSS. The rubric list is read from this file and posted to THSS for item scoring configuration
    b. Test packages that are uploaded to the support tool are persisted for archival purposes

## Build
To build all modules, use the "parent" `pom.xml` that is contained in the `TDS_SupportTool` directory:

* `mvn clean install -f /path/to/parent/pom.xml`

To build the **client**:

* `mvn clean install -f /path/to/client/pom.xml`

To build the **service**:

* `mvn clean install -f /path/to/service/pom.xml`

To build the service and run integration tests:
  
* `mvn clean install -Dintegration-tests.skip=false -f /path/to/service/pom.xml`

### Docker Support
The Assessment Package Loader provides a `Dockerfile` for building a Docker image and a `docker-compose.yml` for running a Docker container that hosts the service `.jar`.  For the following command to work, the Docker Engine must be installed on the target build machine.  Resources for downloading and installing the Docker Engine on various operating systems can be found [here](https://docs.docker.com/engine/installation/).  For details on what Docker is and how it works, refer to [this page](https://www.docker.com/what-docker).

To build the service and its associated Docker image:

* `mvn clean install docker:build -f /path/to/service/pom.xml`

### Run .JAR
To run the compiled jar built by one of the build commands above, use the following:

```
java -Xms256m -Xmx512m \
    -jar /path/to/target/tds-support-tool-service-0.0.1-SNAPSHOT.jar \
    --server-port="8080"
```

#### Additional Details for Interacting With Docker
The `Dockerfile` included in this repository is intended for use with [Spotify's Docker Maven plugin](https://github.com/spotify/docker-maven-plugin).  As such, the `docker build` command will fail because it cannot find the compiled `.jar`.

The Docker container can be started via `docker-compose` or `docker run`:

* The command for starting the container via `docker-compose`:  `docker-compose up -d -f /path/to/docker-compose.yml`
  * **NOTE:** If `docker-compose` is run in the same directory where the `docker-compose.yml` file is located, `docker-compose up -d` is sufficient to start the container
* Alternately, `docker run` can be used to start up the container:  `docker run -d -p [open port on host]:8080 --env-file /path/to/apl.env fwsbac/tds-support-tool-service`
  * example:  `docker run -d -p 23572:8080 --env-file support-tool.env fwsbac/tds-support-tool-service`

To see the list of running Docker containers, use the following command:

* `docker ps -a`
* Output will appear as follows:
 
```
CONTAINER ID        IMAGE                        COMMAND                CREATED             STATUS              PORTS                     NAMES
de37db84cb30        fwsbac/tds-support-tool-service   "/docker-startup.sh"   2 hours ago         Up 2 hours          0.0.0.0:23489->8080/tcp   docker_support-tool_1
```
To tail the log files for the process(es) running on the Docker container:

* `docker logs -f [container id]`
  * **NOTE:**  To view the logs without tailing them, omit the `-f` from the command above
* example:  `docker logs -f de37db84cb30`

## Spring Configuration
The following is a brief explanation of the Spring properties that are available for the support tool service.  You can edit and alter these with Spring cloud configuration and in the `application.yml` file within the main src for local development

```
management.security.roles: MANAGEMENT #Default security role mainly for health endpoints

---
# Contains properties for the tds-support-tool-service.

server:
  port: # the port to run tomcat on - default is 8080
  context-path: # (Optional) the path to run the support tool application on (usually "/supporttool"

management:
  port: 8008 # the health and other actuator endpoints will run on "8008" while the web app runs on `server.port`
  security:
    enabled: false
  health:
    redis:
      enabled: false
    rabbit:
      enabled: false

spring:
  resources:
    add-mappings: true

logstash-destination: # (Optional) the logstash  host, if one is configured for centralized logging

spring:
  rabbitmq:
    addresses: # address of the rabbitmq servers to use
    username: # the rabbitmq user, usually "services" when running on Kubernetes
    password: # the password for the rabbitmq user
  data:
    mongodb:
      host: # hostname of the mongodb instance to use. This mongo server will store state data for all support tool jobs
      port: # the mongodb port (usually `27017`)
      database: support-tool
      username: # the mongodb username (with read and write privilages to the "support-tool" database
      password: # the mongodb password
  http:
    multipart:
      max-file-size: # the maximum file size to allow for test package uploads
      max-request-size: # the maximum file size to allow for support tool requests

# URLs for dependent services
support-tool:
  art-rest-url: # the ART rest endpoint root (e.g., `http://<ART HOSTNAME>/rest`)
  assessment-url: # the Assessment Service API root (e.g., `http://tds-assessment-service`)
  tis-api-url: # the Test Integration System API root (e.g., `http://<TIS HOSTNAME>/api`)
  thss-api-url: # the Teeacher Handscoring System API root (e.g., `http://<THSS HOSTNAME>/api`)
  progman-url: # the Program Management API root (e.g., `http://<PROGMAN HOST>/rest`)

  progman-tenant-level: # the progman tenant level - either `DISTRICT`, `STATE`, `INSTITUTION`, or `CLIENT`
  progman-tenant: # the progman tenant to load test packages into - refer to ART documentation for more information about tenancy

  sso-client-id: # the SSO client id to use for authorized REST calls
  sso-client-secret: # the SSO client secret to use for authorized REST calls
  sso-username: # the SSO username used to create authorized OAuth REST calls to external services
  sso-password: # the password of the SSO/OAuth user
  sso-url: # the url for obtaining an SSO access token (e.g., `https://<SSO HOST>/auth/oauth2/access_token?realm=/sbac`)


data:
  s3:
    accessKey: # An AWS access key for a user that has read access to s3
    secretKey: # The corresponding AWS Secret key for the user that has read access to S3
    bucket-name: # Name of the bucket where loaded test packages should be stored
    test-package-prefix: # Path prefix of where the test packages should be stored

```