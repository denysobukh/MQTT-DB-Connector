# MQTT-DB-Connector

[![Java](https://img.shields.io/badge/Java-11-blue.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.4.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

MQTT-DB-Connector is a Spring Boot service that subscribes to an MQTT topic,
parses weather sensor XML payloads, validates sensor readings, and persists the
accepted measurements with JPA/Hibernate.

## Features

- Subscribes to a configurable MQTT broker and topic using Eclipse Paho.
- Parses XML messages containing timestamped weather sensor readings.
- Stores messages and normalized parameter names with JPA/Hibernate.
- Filters out implausible values before persistence.
- Includes a command-line importer for replaying captured MQTT payloads from a
  file.

## Message Format

The parser expects an XML payload with a `message` element. The `time` attribute
must use the `yyyy-MM-dd HH:mm:ss Z` format.

```xml
<message time="2018-10-06 12:42:54 +0300" from="sensor-1" to="gateway" rssi="-78">
  <temperature>22.2</temperature>
  <humidity>58.7</humidity>
  <pressure>100529</pressure>
  <voltage>9.1</voltage>
</message>
```

Supported values are stored only when they pass these ranges:

| Parameter | Accepted range |
| --- | --- |
| `temperature` | `-20` to `40` degrees Celsius |
| `humidity` | `0` to `101` percent |
| `pressure` | `96000` to `110000` pascals |
| `voltage` | `0` to `10` volts |

## Requirements

- Java 11
- Maven Wrapper included in the repository
- MQTT broker, for example Mosquitto
- A JDBC database supported by Hibernate

The default configuration uses an in-memory H2 database for local development.
Use a Java 11 JDK when building this project; newer JDKs may require dependency
updates for Lombok and Spring Boot.

## Configuration

Application settings are stored in
[`src/main/resources/application.properties`](src/main/resources/application.properties).

```properties
mqtt.broker=tcp://nas.loc:1884
mqtt.topic=sensor/weather
mqtt.clientid=messages_processing_dev

spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=create
```

For a persistent database, replace the datasource properties with your database
URL, driver, username, and password, then add the matching JDBC driver dependency
to [`pom.xml`](pom.xml).

## Build and Test

```bash
./mvnw clean test
```

Build the runnable Spring Boot jar:

```bash
./mvnw clean package
```

## Run

Start the connector with Maven:

```bash
./mvnw spring-boot:run
```

Or run the packaged jar:

```bash
java -jar target/mqtt2dbconnector-0.0.1-SNAPSHOT.jar
```

The service connects to the configured MQTT broker, subscribes to the configured
topic with QoS 2, and retries failed connections with exponential backoff.

## Inspect MQTT Traffic

To watch all MQTT topics with Mosquitto tools:

```bash
mosquitto_sub -h <broker-host> -p <broker-port> -t "#"
```

To publish a sample message:

```bash
mosquitto_pub -h <broker-host> -p <broker-port> -t sensor/weather -m '<message time="2018-10-06 12:42:54 +0300"><temperature>22.2</temperature><humidity>58.7</humidity><pressure>100529</pressure><voltage>9.1</voltage></message>'
```

## Import Captured Messages

`LoadFromFileApplication` is a legacy command-line importer for previously
captured payloads. Each non-empty line in the input file should contain one XML
message. The importer uses standalone Hibernate configuration, so provide a
`hibernate.cfg.xml` on the runtime classpath before using it.

```bash
./mvnw -DskipTests compile dependency:build-classpath -Dmdep.outputFile=target/classpath.txt
java -cp "target/classes:$(cat target/classpath.txt)" io.github.denysobukh.mqtt2dbconnector.LoadFromFileApplication messages.txt
```

The importer de-duplicates messages by timestamp before inserting them.

## Project Structure

```text
src/main/java/io/github/denysobukh/mqtt2dbconnector/
|-- Application.java                 # Spring Boot entry point
|-- MqttListener.java                # MQTT callback and persistence flow
|-- SensorMessageBuilder*.java       # Payload parser contract and XML parser
|-- model/                           # JPA entities
|-- service/ConnectorService.java    # MQTT connection lifecycle
`-- validator/                       # Parameter validation rules
```

## Database Scripts

The [`scripts`](scripts) directory contains SQL and helper scripts used during
local database cleanup and station setup.

## License

This project is licensed under the [MIT License](LICENSE).
