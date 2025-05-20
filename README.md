[![CI](https://github.com/huberchrigu/give-me-features/actions/workflows/gradle.yml/badge.svg)](https://github.com/huberchrigu/give-me-features/actions/workflows/gradle.yml)

# Use cases

![Use cases](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/huberchrigu/give-me-features/refs/heads/master/docs/use-cases.plantuml)

# Known issues

* See `TODO`s in code

# Architectural decisions
## Immutability

All domain models are immutable, which is easy to do with Kotlin and is a natural fit to functional style.

## Cloud-ready

This application is cloud-ready. It does not contain any internal state and can easily be scaled horizontally.

## Optimistic locking

We use Spring Data to implement optimistic locking.

## Versioned Aggregates

* The following capabilities shall be isolated, such that they can easily be reused independently of each other:
  * An **aggregate root** describes the entry point of an aggregate with an `id` and a `version`.
  * The `history` package supports versioning an aggregate in a separate collection, such that the main aggregate collection can be queried and used as normal.

![Use cases](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/huberchrigu/give-me-features/refs/heads/master/docs/aggregate-root.plantuml)
