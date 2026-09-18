# Test management and execution

- **ID:** Q-001
- **Status:** Draft
- **Use cases:** Testing a feature; describing, linking, executing tests
- **Owner:** Quality Manager and Development team

## Goal

Allow quality work to be specified, linked to delivery artifacts, executed, and
used as evidence for delivery decisions.

## Scope

Define test cases, artifact links, executable/manual classification, and
immutable test runs. External runner integration and interface links are
excluded from the first slice.

## Domain rules

- A test case has stable identity, title, purpose, preconditions, steps,
  expected result, type, and lifecycle state.
- Test cases may link to multiple features and tasks.
- A run records case/version, executor, environment, times, outcome, evidence,
  and retry relationship; past runs are immutable.
- Executability is distinct from a run result and identifies automation and
  required environment/data.

## Acceptance scenarios

### Scenario: Specify a linked test case

**Given** a quality manager

**When** they create a test case linked to a feature and task

**Then** its details and links are persisted and visible from each item.

### Scenario: Record a manual test run

**Given** a runnable test case

**When** a tester records a passed or failed execution with evidence

**Then** an immutable run appears in its history and feature test summary.

## Open questions

- Does `testing-plugin` remain optional or become a core module?
- Which outcomes and evidence retention policy are required?
- Which external runner, if any, should be integrated?

## Traceability

- **Code:** Existing in-progress `testing-plugin`, `plugin-api`, and planned test-case/test-run modules.
- **Tests:** Existing `TestDefinitionTest` and `TestingTouchpointFactoryTest`; planned persistence, UI, and run-history tests.
- **ADRs:** Planned plugin ownership, link cardinality, and evidence-retention ADRs.
