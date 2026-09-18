---
name: spec-test-design
description: Design or implement automated verification from a repository specification's acceptance scenarios.
---

Read acceptance scenarios before choosing test layers. Use domain tests for
invariants, Mongo/history integration tests for persistence and concurrency,
controller tests for HTTP behavior, and Playwright tests for visible HTMX flows.

Cover permitted and rejected state transitions. Do not claim coverage because a
nearby behavior is tested. Preserve test style and deterministic substitutes for
external systems unless real integration is explicitly required. Update concrete
test links in Traceability after work, and run relevant Gradle tests when Java
is available; otherwise state what could not run and why.
