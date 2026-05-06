# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

When a new release is proposed:

1. Create a new branch `bump/x.x.x` (this isn't a long-lived branch!!!);
2. The Unreleased section on `CHANGELOG.md` gets a version number and date;
3. Open a Pull Request with the bump version changes targeting the `main` branch;
4. When the Pull Request is merged, a new Git tag must be created using [GitHub environment](https://github.com/rios0rios0/rest-arch/tags).

Releases to productive environments should run from a tagged version.
Exceptions are acceptable depending on the circumstances (critical bug fixes that can be cherry-picked, etc.).

## [Unreleased]

## [0.2.2] - 2026-05-06

### Changed

- changed the Java dependencies to their latest versions

## [0.2.1] - 2026-04-29

### Changed

- changed the Java dependencies to their latest versions

## [0.2.0] - 2026-04-28

### Added

- added `CLAUDE.md` with build commands, architecture notes, and conventions for Claude Code sessions

### Changed

- refreshed `.github/copilot-instructions.md` to reflect Java 21, Spring Boot 3.4, updated dependency versions, and library (non-bootable) project status

## [0.1.1] - 2026-04-15

### Changed

- changed the Java dependencies to their latest versions

## [0.1.0] - 2026-03-13

### Added

- added GitHub Actions workflow for CI/CD pipeline

### Changed

- changed Apache HttpComponents from `4.x` to `5.x` for compatibility with Spring Boot `3.x`
- changed dependency versions to use Spring Boot managed versions where possible (`spring-context-support`, Jackson, Gson)
- changed Java version from `1.8` to `21` to match CI environment
- changed OkHttp from `4.9.2` to `4.12.0`, Guava from `32.0.0-jre` to `33.4.0-jre`, Joda-Time from `2.9.9` to `2.13.0`, JUnit from `4.13.1` to `4.13.2`
- changed Spring Boot parent from `2.1.0.RELEASE` to `3.4.13` to resolve dependency vulnerabilities

### Fixed

- fixed `CVE-2020-29582` false positive for `kotlin-stdlib` by adding a `dependency-check` suppression file
- fixed `CVE-2025-68161` by overriding `log4j2.version` to `2.25.3` (Socket Appender TLS hostname verification)
- fixed CI `maven-verify` and `sast:codeql` failures by removing `spring-boot-maven-plugin` since this is a library project, not a bootable application
- fixed OWASP Dependency-Check build failure by adding the `dependency-check-maven` plugin with NVD API configuration
- fixed package declarations in `RestService.java` and `ObjectNotFoundException.java` to match directory structure (`com.services` instead of `com`)
- fixed Trivy SCA vulnerabilities by overriding tomcat (`10.1.52`), logback (`1.5.32`) and jackson-bom (`2.18.6`) managed versions

### Removed

- removed `spring-boot-maven-plugin` from build configuration (unnecessary for a library project)
- removed Travis CI configuration in favor of GitHub Actions
