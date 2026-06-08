# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

`rest-arch` is a Java 21 / Spring Boot 3.5 **library** (not a bootable application). It provides an abstract `RestService<T extends Serializable>` base class for consuming REST APIs. There is no `spring-boot-maven-plugin` — you cannot run `mvn spring-boot:run`.

## Build and test

```bash
mvn clean install   # compile + test
mvn test            # tests only
mvn package -DskipTests  # JAR without tests
```

## Architecture

- `RestService<T>` resolves its generic type via reflection at construction time. Concrete subclasses inherit typed `getForEntity`, `getForList`, `postForEntity`, `postForList` methods.
- Base URL injected via `@Value("${app.restservice.base}")`.
- `ObjectNotFoundException` thrown on 404; error messages resolved through `MessageSource` + `LocaleContextHolder`.
- Package root is `com`; services live under `com.services`.

## Key dependencies

Spring Boot 3.5.14, OkHttp 5.3.2, Apache HttpComponents Client 5.x, Guava 33.6.0-jre, Joda-Time 2.14.2, JUnit 4.13.2. Jackson and Gson versions are Spring Boot managed.

## Conventions

- Conventional Commits (`feat:`, `fix:`, `chore:`) following [rios0rios0 Git Flow](https://github.com/rios0rios0/guide/wiki/Life-Cycle/Git-Flow).
- `null` checks use `Objects.isNull` / `Objects.nonNull`.
- SLF4J logging via `LoggerFactory.getLogger(getClass())`.

## CI

GitHub Actions (`.github/workflows/default.yaml`) delegates to `rios0rios0/pipelines/.github/workflows/maven-library.yaml@main`.
