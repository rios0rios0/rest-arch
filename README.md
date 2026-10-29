<h1 align="center">REST Arch</h1>
<p align="center">
    <a href="https://github.com/rios0rios0/rest-arch/releases/latest">
        <img src="https://img.shields.io/github/release/rios0rios0/rest-arch.svg?style=for-the-badge&logo=github" alt="Latest Release"/></a>
    <a href="https://github.com/rios0rios0/rest-arch/blob/main/LICENSE">
        <img src="https://img.shields.io/github/license/rios0rios0/rest-arch.svg?style=for-the-badge&logo=github" alt="License"/></a>
    <a href="https://github.com/rios0rios0/rest-arch/actions/workflows/default.yaml">
        <img src="https://img.shields.io/github/actions/workflow/status/rios0rios0/rest-arch/default.yaml?branch=main&style=for-the-badge&logo=github" alt="Build Status"/></a>
</p>

A RESTful architecture reference project built with Java and Spring Boot, demonstrating clean REST service patterns.

## Features

- Spring Boot-based REST service skeleton
- Maven build system targeting Java 21 and Spring Boot 4
- Pre-configured application properties and test setup

## Project Structure

```
src/
  main/
    java/com/services/RestService.java    # REST service implementation
    resources/application.properties      # Application configuration
  test/
    java/com/ApplicationTest.java         # Spring Boot entry point for tests
pom.xml                                   # Maven project descriptor
```

## Installation

```bash
git clone https://github.com/rios0rios0/rest-arch.git
cd rest-arch
mvn clean install
```

## Usage

`rest-arch` is a library JAR, not a bootable application, so there is no `mvn spring-boot:run`. After `mvn clean install`, add it as a dependency of your Spring Boot application, then:

1. Create a `@Service` class in package `com.services` (the `RestService` base class and its methods are package-private) that extends `RestService<YourEntity>`, where `YourEntity implements Serializable`.
2. Call the inherited `getForEntity`, `getForList`, `postForEntity`, and `postForList` methods with paths relative to the base URL.
3. Set `app.restservice.base` to the remote API's base URL in your application's configuration.

## Contributing

Contributions are welcome. See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the terms specified in the [LICENSE](LICENSE) file.
