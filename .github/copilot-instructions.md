# Copilot Instructions for rest-arch

## Project Overview

`rest-arch` is a RESTful architecture reference **library** built with **Java 21** and **Spring Boot 3.5**. It provides an abstract `RestService<T>` base class that encapsulates common patterns for consuming REST APIs (GET, POST, JSON deserialization, error handling), intended to be extended by concrete service classes in applications that integrate with external REST backends. This is a library JAR, not a bootable application — there is no `spring-boot-maven-plugin`.

## Repository Structure

```
.github/
  workflows/
    default.yaml              # CI/CD pipeline (delegates to shared pipeline)
src/
  main/
    java/com/services/
      ObjectNotFoundException.java  # Custom exception for 404 responses
      RestService.java              # Abstract generic REST client base class
    resources/
      application.properties   # Spring Boot application configuration (port 8080)
  test/
    java/com/
      ApplicationTest.java     # Spring Boot application entry point for tests
pom.xml                        # Maven project descriptor
CONTRIBUTING.md                # Contribution guidelines and development workflow
CHANGELOG.md                   # Version history following Keep a Changelog
```

## Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.5.14 (spring-boot-starter-web)
- **Build**: Apache Maven
- **HTTP clients**: Spring `RestTemplate`, OkHttp 5.4.0, Apache HttpComponents Client 5.x
- **JSON**: Jackson Databind, Gson (Spring Boot managed)
- **Utilities**: Guava 33.6.0-jre, Joda-Time 2.14.2
- **Testing**: JUnit 4.13.2, spring-boot-starter-test
- **CI/CD**: GitHub Actions — delegates to `rios0rios0/pipelines/.github/workflows/maven-library.yaml@main`

## Build, Test, and Run Commands

```bash
# Install dependencies and compile (runs tests by default)
mvn clean install

# Run only the test suite
mvn test

# Package the library JAR without running tests
mvn package -DskipTests
```

## Architecture and Design Patterns

- **Abstract generic service**: `RestService<T extends Serializable>` uses Java generics and reflection to auto-resolve the entity type at construction time. Concrete subclasses annotated with `@Service` inherit typed `getForEntity`, `getForList`, `postForEntity`, and `postForList` methods.
- **Configuration via `@Value`**: The base URL for the remote REST API is injected through `${app.restservice.base}` in `application.properties`.
- **Centralized error handling**: `HttpClientErrorException` is caught in `getRequest`/`postRequest`; 404 errors throw an `ObjectNotFoundException` with a localized message; other errors are logged.
- **Internationalization**: Error messages are resolved via `MessageSource` using the current locale (`LocaleContextHolder`).
- **Date normalisation in query params**: `verifyMap` serialises `Date` values as `yyyy-MM-dd` strings before appending them to query strings.

## CI/CD Pipeline

The `.github/workflows/default.yaml` triggers on pushes and PRs to `main`, on all tags, and on manual dispatch. It reuses the organisation-wide reusable workflow at `rios0rios0/pipelines/.github/workflows/maven-library.yaml@main`, which handles compilation, testing, and artefact publishing automatically.

## Dependency Security

- The build wires in OWASP `dependency-check-maven` (12.2.0) with `failBuildOnCVSS=7` and a `dependency-check-suppression.xml`. Run the scan with `mvn dependency-check:check` (requires `NVD_API_KEY`, which CI supplies as a secret).
- `pom.xml` `<properties>` pin overrides on top of the Spring Boot 3.5.14 managed versions (`log4j2`, `spring-framework`, `httpcore5`/`httpclient5`) specifically to remediate CVEs. The inline comments are authoritative — some pins carry "do NOT bump" warnings (notably `log4j2` must stay on stable `2.26.1` rather than a `3.0.0-beta*`). Do not suggest raising these to "latest" without checking the comment; a naive upgrade reintroduces known vulnerabilities.

## Development Workflow

1. Fork the repository and create a feature branch: `git checkout -b feat/my-change`
2. Build and verify: `mvn clean install`
3. Run tests: `mvn test`
4. Commit using [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`, `fix:`, `chore:`, etc.) following the [rios0rios0 Git Flow guide](https://github.com/rios0rios0/guide/wiki/Life-Cycle/Git-Flow)
5. Open a pull request against `main`

## Coding Conventions

- Class and method names follow standard Java naming conventions (PascalCase for classes, camelCase for methods).
- `@SuppressWarnings("unchecked")` is used on the abstract class due to the generic type-cast in the constructor.
- Package root is `com`; services live under `com.services`.
- Logging uses SLF4J (`LoggerFactory.getLogger(getClass())`).
- `null` checks prefer `Objects.isNull` / `Objects.nonNull` over direct `== null` comparisons.
- Commented-out code (e.g., `putForEntity`, `delete`) is retained as reference; remove or implement when adding new HTTP verb support.

## Common Tasks

### Adding a new REST entity service
1. Create a class in `src/main/java/com/services/` annotated with `@Service`.
2. Extend `RestService<YourEntity>` where `YourEntity implements Serializable`.
3. Use the inherited `getForEntity`, `getForList`, `postForEntity`, `postForList` methods, passing relative URL paths.

### Changing the base URL
Set `app.restservice.base` in `src/main/resources/application.properties` (or the appropriate environment-specific properties file).

### Releasing a new version
1. Create a branch `bump/x.x.x`.
2. Compile the fragments pending under `.changes/unreleased/` into a version section with `chlog batch auto && chlog merge` — never edit `CHANGELOG.md` by hand (AutoBump does this for you: it reads the fragments directly).
3. Open a PR against `main`; merge it.
4. Create a Git tag via the [GitHub tags page](https://github.com/rios0rios0/rest-arch/tags).

<!-- chlog:start -->
## Changelog (chlog) — MANDATORY

If the repository you are working in uses chlog (a `.chlog.yaml` or `.chlog.yml`
config file, or a `.changes/` directory, exists at the project root), the
following is binding and ALWAYS applies: whenever you make ANY change, you MUST
create a changelog fragment as part of the same change — automatically, without
being asked, before committing.

- Do NOT edit CHANGELOG.md directly; it is generated from fragments.
- Create the fragment with:
  `chlog new --kind <Kind> --body "<imperative description>"`
- Valid kinds: Added, Changed, Deprecated, Removed, Fixed, Security
- Choose the kind that best matches the change (e.g., new feature → Added,
  bug fix → Fixed, behavior change → Changed, removal → Removed, security fix → Security).
- If the change is backward-INCOMPATIBLE with the public API (a breaking
  change), you MUST add the `--breaking` flag:
  `chlog new --kind <Kind> --breaking --body "<description>"`.
  This is the ONLY thing that triggers a major version bump — the kind alone
  never does (per SemVer, major = incompatible change). When unsure whether a
  change breaks compatibility, ask the user instead of guessing.
- Fragments are YAML files in `.changes/unreleased/`; stage them with your commit.
- `chlog check` fails the build when a fragment is missing — never skip it.
<!-- chlog:end -->
