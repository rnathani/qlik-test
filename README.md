Overview
===========================

Application manages messages and provide information whether message is a palindrom or not.

## Architecture
[![Screen-Shot-2020-10-02-at-9-42-02-PM.png](https://i.postimg.cc/SKnsLtnY/Screen-Shot-2020-10-02-at-9-42-02-PM.png)](https://postimg.cc/Mc8qq5bz)

Message Manager application is a springboot application exposing CRUD operations to manage messages over HTTP. Application is powered by DynamoDB.

## Prerequisite
Following prerequisites are required to build application locally:
1. Maven 3.6.2
2. Java 11 (openjdk 11 2018-09-25)
3. Docker 2.4.0.0 Channel Stable. It must be running, tests are using amazon/dynamodb-local image.

## Build:
command: mvn clean install -U

## Test:
command: mvn test

### Build and Run<a id="build-run"></a>

To run the application from command line, you may run one of the following commands after building your application:
```bash
**start: Amazon dynamoDB local**
docker run -p 8000:8000 amazon/dynamodb-local

**Start Application**
 mvn spring-boot:run  \
-Dspring-boot.run.arguments="--amazon.dynamodb.endpoint=http://localhost:8000" 
```

Open a browser and hit http://localhost:8080/ for service spec.

### Build and Run using Docker

```bash
mvn clean install -U && docker-compose up
```

Open a browser and hit http://localhost:8080/ for service spec.

### Documentation:

Application is using swagger documentation which can be viewed by running application locally or on the server.

Open a browser and hit http://qlik-elb-60941415.us-west-2.elb.amazonaws.com/swagger-ui.html application will redirect to swagger documentation.
