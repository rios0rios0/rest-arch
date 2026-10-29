# Contributing

Contributions are welcome. By participating, you agree to maintain a respectful and constructive environment.

For coding standards, testing patterns, architecture guidelines, commit conventions, and all
development practices, refer to the **[Development Guide](https://github.com/rios0rios0/guide/wiki)**.

## Prerequisites

- [JDK](https://adoptium.net/) 21+
- [Apache Maven](https://maven.apache.org/) 3.6+

## Development Workflow

1. Fork and clone the repository
2. Create a branch: `git checkout -b feat/my-change`
3. Install dependencies and build the project:
   ```bash
   mvn clean install
   ```
4. Run the test suite:
   ```bash
   mvn test
   ```
5. Package the library JAR (this is a library, not a bootable application; there is no `spring-boot:run`):
   ```bash
   mvn package
   ```
6. Commit following the [commit conventions](https://github.com/rios0rios0/guide/wiki/Git-Flow)
7. Open a pull request against `main`
