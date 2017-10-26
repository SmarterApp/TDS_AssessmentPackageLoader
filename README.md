# TDS Assessment Package Loader
## Overview
The `TDS_AssessmentPackageLoader` consists of two modules:

* **client:** Contains the POJOs/classes needed for a consumer to interact with the Assessment Package Loader
* **service:** Contains the loader application service code


## Setup
The following tools will be needed to build the project.

1. Maven
2. Java 8 

## Build
To build the **client** and **service**, use the "parent" `pom.xml` that is contained in the `TDS_AssessmentPackageLoader` directory:

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
    -jar /path/to/target/tds-apl-service-0.0.1-SNAPSHOT.jar \
    --server-port="8080" \
    --server.undertow.buffer-size=16384 \
    --server.undertow.buffers-per-region=20 \
    --server.undertow.io-threads=64 \
    --server.undertow.worker-threads=512 \
    --server.undertow.direct-buffers=true 
```

#### Additional Details for Interacting With Docker
The `Dockerfile` included in this repository is intended for use with [Spotify's Docker Maven plugin](https://github.com/spotify/docker-maven-plugin).  As such, the `docker build` command will fail because it cannot find the compiled `.jar`.

The Docker container can be started via `docker-compose` or `docker run`:

* The command for starting the container via `docker-compose`:  `docker-compose up -d -f /path/to/docker-compose.yml`
  * **NOTE:** If `docker-compose` is run in the same directory where the `docker-compose.yml` file is located, `docker-compose up -d` is sufficient to start the container
* Alternately, `docker run` can be used to start up the container:  `docker run -d -p [open port on host]:8080 --env-file /path/to/apl.env fwsbac/tds-apl-service`
  * example:  `docker run -d -p 23572:8080 --env-file apl.env fwsbac/tds-apl-service`

To see the list of running Docker containers, use the following command:

* `docker ps -a`
* Output will appear as follows:
 
```
CONTAINER ID        IMAGE                        COMMAND                CREATED             STATUS              PORTS                     NAMES
de37db84cb30        fwsbac/tds-apl-service   "/docker-startup.sh"   2 hours ago         Up 2 hours          0.0.0.0:23489->8080/tcp   docker_loader_1
```
To tail the log files for the process(es) running on the Docker container:

* `docker logs -f [container id]`
  * **NOTE:**  To view the logs without tailing them, omit the `-f` from the command above
* example:  `docker logs -f de37db84cb30`