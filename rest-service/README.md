# Connect 4 game: rest service 

This module provides HTTP service which exposes REST API for the game.

## Usage

When service is up and running it accepts HTTP requests on `localhost:8080`.

### How to discover API

API documentation which describes all service endpoints is available in JSON and XML formats and it follows [Swagger specification](http://swagger.io/specification).

There is also [Swagger UI](http://swagger.io/swagger-ui) embedded in the service which helps discover API. To use it please open `http://localhost:8080/swagger` in your favorite browser.

## Contributing

Note: Please make sure all requirements mentioned on the [project page](../README.md) are met.

### How to build it

To build the project please execute following command:

```
mvn clean package
```

When build process is completed then `rest-service-1.0.0-SNAPSHOT.jar` JAR will be available in `target` directory.

### How to run it

To run REST service please execute following command from module root directory:

```
java -jar target/rest-service-1.0.0-SNAPSHOT.jar server
```

Above command will launch HTTP service on port `8080`.
