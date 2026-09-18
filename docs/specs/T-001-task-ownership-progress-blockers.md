# Task ownership, progress, and blockers

- **ID:** T-001
- **Status:** Draft
- **Use cases:** Adding task progress; tackling blockers; assigning tasks
- **Owner:** Product Owner and Development team

## Goal

Make active task execution actionable by recording ownership, progress, and
blocker resolution.

## Scope

Add assignment, structured progress updates, and first-class blocker records
while preserving the existing task status state machine. Identity-provider
selection and feature completion policy are excluded.

## Domain rules

- A task has one accountable assignee; contributors remain an open decision.
- Progress updates are append-only and attributed, with timestamp and selected
  progress measure.
- A blocker has description, status, owner, timestamps, resolution, and links
  to affected tasks.
- Resolving a blocker does not implicitly reopen a task unless approved.

## Acceptance scenarios

### Scenario: Assign and update a task

**Given** an authorized user and an open task

**When** the task is assigned and a progress update is posted

**Then** the task shows its assignee and chronological attributed updates.

### Scenario: Resolve a blocker

**Given** a blocked task with an open blocker

**When** its blocker is resolved

**Then** resolution is retained and the defined next transition is offered.

## Open questions

- Which identity provider and user/team model will be used?
- Is progress percentage, remaining estimate, checklist, or a combination?
- Can a task be `DONE` with open blockers?

## Traceability

- **Code:** Planned `tasks` aggregates, persistence, controllers/templates, and identity boundary.
- **Tests:** Planned transition, authorization, and UI tests.
- **ADRs:** Planned identity/ownership and blocker-lifecycle ADRs.
