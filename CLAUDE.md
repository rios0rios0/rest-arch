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

Spring Boot 3.5.14, OkHttp 5.4.0, Apache HttpComponents Client 5.x, Guava 33.6.0-jre, Joda-Time 2.14.2, JUnit 4.13.2. Jackson and Gson versions are Spring Boot managed.

## Dependency security

- OWASP `dependency-check-maven` (12.2.0) is wired into the build with `failBuildOnCVSS=7` and a `dependency-check-suppression.xml`. Run it with `mvn dependency-check:check` (needs `NVD_API_KEY`; CI passes it as a secret).
- Several versions are pinned in `pom.xml` `<properties>` (`log4j2`, `spring-framework`, `httpcore5`/`httpclient5`) to override the Spring Boot 3.5.14 managed versions and clear flagged CVEs. Read the inline comments before changing them — some carry explicit "do NOT bump" warnings (e.g. `log4j2` must stay on stable `2.26.1`, not a `3.0.0-beta*`, which reintroduces the CVEs). A blind dependency upgrade will regress the security posture.

## Conventions

- Conventional Commits (`feat:`, `fix:`, `chore:`) following [rios0rios0 Git Flow](https://github.com/rios0rios0/guide/wiki/Life-Cycle/Git-Flow).
- `null` checks use `Objects.isNull` / `Objects.nonNull`.
- SLF4J logging via `LoggerFactory.getLogger(getClass())`.

## CI

GitHub Actions (`.github/workflows/default.yaml`) delegates to `rios0rios0/pipelines/.github/workflows/maven-library.yaml@main`.

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
