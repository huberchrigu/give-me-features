# Architecture

Give Me Features is a Kotlin/Spring Boot application for managing features and
their tasks. It is built as a modular monolith: modules run in one application
process, but their responsibilities and dependencies are kept explicit.

## Contents

- [Runtime shape](#runtime-shape)
- [Gradle modules](#gradle-modules)
- [Business modules](#business-modules)
- [Persistence and history](#persistence-and-history)
- [Plugin model](#plugin-model)
- [Security and configuration](#security-and-configuration)
- [Testing and delivery](#testing-and-delivery)

## Runtime shape

```text
Browser
  |
  | HTTP / HTMX requests
  v
Spring WebFlux controllers
  |
  +--> application and domain services
  |       |
  |       +--> repositories and history support
  |       |       +--> plugins
  |
  v
JTE templates / HTML fragments

MongoDB <--- reactive Spring Data repositories
```

The UI is server-rendered with JTE templates. HTMX requests fetch or replace
HTML fragments, so the browser does not contain a separate single-page
application. CoreUI and WebJars provide client-side assets.

## Gradle modules

| Module | Responsibility |
| --- | --- |
| Root project | The Spring Boot application and its business modules, web UI, security, persistence, and integration tests. |
| `shared-api` | Small shared contracts, especially aggregate-root abstractions used across module boundaries. |
| `plugin-api` | Plugin contracts: plugin metadata, feature/task touchpoints, form fields, triggers, references, and persistence adapters. |
| `testing-plugin` | An optional Spring Boot auto-configuration that contributes a testing-status plugin backed by MongoDB. |

The root application depends on `plugin-api`. Plugins depend on `plugin-api`
and can be included as application dependencies. The testing plugin is currently
a test dependency of the root project while it is being developed.

## Business modules

The root project is declared as a Spring Modulith application. Its main
business areas are:

| Area | Purpose |
| --- | --- |
| `features` | Feature aggregate, application service, web endpoints, persistence, and history merging. |
| `tasks` | Task aggregate, application/domain services, web endpoints, persistence, and history merging. |
| `epics` | Epic model and repository. |
| `plugins` | Discovers registered plugins, stores plugin status/data, builds plugin forms, and exposes plugin UI endpoints. |
| `shared` | Cross-cutting web, security, MongoDB, aggregate, history, Markdown, and HTML-sanitizing support. |

Feature and task modules own their respective aggregates. Controllers translate
HTTP input into service calls and select JTE views; persistence details remain
in repository packages.

## Persistence and history

MongoDB is the system of record, accessed through reactive Spring Data MongoDB
repositories and Kotlin coroutines where appropriate. Domain models are
immutable Kotlin data classes.

Mutable aggregate state is protected by optimistic locking. The application also
maintains historical versions in separate collections: the current aggregate is
kept convenient to query, while the history support records prior versions and
merges changes when needed.

## Plugin model

A plugin is a `Plugin` with a stable ID, a title, and optional touchpoints for
features and tasks.

```text
Plugin auto-configuration
  -> Plugin bean
      -> feature/task ItemDefinition
          -> form fields + value mapper + change trigger + repository
```

An `ItemDefinition` tells the host how to render a plugin form, map form values
to a plugin-specific entity, persist that entity, and react when its parent
feature or task changes. Plugin entities live in plugin-owned persistence.

`testing-plugin` illustrates this extension point. It registers a `testing`
plugin, stores a `TestDefinition` per feature or task, and exposes its test
state (`NOT_STARTED`, `RUNNING`, or `FINISHED`). Its auto-configuration is
published through Spring Boot's `AutoConfiguration.imports` mechanism.

## Security and configuration

Spring Security protects the application, including HTMX-aware handling for
access-denied responses and redirects. The current security configuration uses
development users; real user management remains a planned enhancement.

`application.yml` names the application and selects the `local` profile by
default. Development tooling enables LiveReload. Environment-specific MongoDB
and security settings should be supplied through Spring profiles or external
configuration rather than embedded in domain code.

## Testing and delivery

Tests cover module behavior, MongoDB persistence, history merging, web
controllers, UI behavior, and plugins. UI tests use Playwright. GitHub Actions
builds with JDK 21, installs Playwright dependencies, runs the Gradle build, and
publishes JUnit and HTML test reports.
