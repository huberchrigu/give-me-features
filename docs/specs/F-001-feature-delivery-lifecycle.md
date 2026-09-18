# Feature delivery lifecycle

- **ID:** F-001
- **Status:** Draft
- **Use cases:** Implementing, completing, and reporting on a feature
- **Owner:** Product Owner and Software Architect

## Goal

Make feature delivery state explicit, rather than inferring it only from task
completion percentage.

## Scope

Add a feature lifecycle, completion criteria, and concise report. Release
scheduling, task assignment, and test-run execution are excluded.

## Domain rules

- Proposed states are `DRAFT`, `READY`, `IN_PROGRESS`, `READY_FOR_RELEASE`,
  `COMPLETED`, and `REOPENED`; approval must settle vocabulary and authority.
- Completion records actor and time and validates agreed criteria.
- A report shows lifecycle state, progress, task-status counts, blocked count,
  and latest meaningful update.

## Acceptance scenarios

### Scenario: Complete a ready feature

**Given** a feature meets approved completion criteria

**When** an authorized user completes it

**Then** its state, completion actor, and timestamp are persisted and reported.

### Scenario: Reject premature completion

**Given** a feature has unmet completion criteria

**When** completion is attempted

**Then** the unmet criterion is shown and the state is unchanged.

## Open questions

- Which roles can perform each transition?
- Must every linked task be `DONE`, or can exceptions be approved?
- Is `READY_FOR_RELEASE` owned by the feature or release model?

## Traceability

- **Code:** Planned `features` aggregate, web views, history merger, and report projection changes.
- **Tests:** Planned transition, persistence/history, and Playwright report tests.
- **ADRs:** Planned lifecycle and completion-criteria ADR.
