## Overview

Spring Boot REST Application Andrew Bauman MPC Demo.

The Application is a server side OptaPlanner application.

---

## How do I use it?

### Prerequisites

Java 8

Apache Maven

### Build the application using Maven

`mvn clean install`

### Run the application

`java -jar target/mpc-demo-0.0.1-SNAPSHOT.jar`

### Alternative

`mvn clean spring-boot:run`

### Running the Tests

Unit tests will be executed during the `test` lifecycle phase and will run as part of any maven goal after `test`.

`mvn package`

### Access the application

To access the application, open the following link in your browser:

`http://localhost:9999`

Swagger UI can be accessed with the following link:

`http://localhost:9999/swagger-ui.html`

### Exposed Endpoints

All exposed endpoints can be found using the [swagger api documentation](http://localhost:9999/v2/api-docs) or the [swagger-ui page](http://localhost:9999/swagger-ui.html).

# mpc-demo
CacheService can be configured to use the local JDG cache
* Download "Data Grid" from https://developers.redhat.com/products/datagrid/download/
* run ./standalone.sh @ [JDG-HOME]/bin
* comment and uncomment jdgServerHost and jdgServerPort lines in CacheService to switch to local JDG instance

