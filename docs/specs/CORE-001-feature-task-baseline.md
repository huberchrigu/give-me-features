# Feature and task management baseline

- **ID:** CORE-001
- **Status:** Implemented
- **Use cases:** Describing a feature; creating, blocking, and completing a task
- **Owner:** Product and engineering team

## Goal

Allow authenticated users to describe features, plan work as linked tasks, keep
task details current, and see resulting feature progress.

## Scope

Features have a name, Markdown description, and linked tasks. Tasks have a
name, Markdown description, and `OPEN`, `BLOCKED`, or `DONE` status. Users can
create tasks from features, edit both entities, change task status, and link an
existing task to a feature. Assignment, feature lifecycle, blocker details,
named tests, collaboration, and reports are excluded.

## Domain rules

- New tasks are `OPEN`.
- `OPEN` transitions to `BLOCKED` or `DONE`; `BLOCKED` to `OPEN` or `DONE`; and
  `DONE` to `OPEN`.
- Feature progress is derived from the percentage of linked tasks in `DONE`.
- Features and tasks use optimistic locking, history, and a merge flow.

## Acceptance scenarios

### Scenario: Describe a feature

**Given** an authenticated user is on the feature list

**When** they submit a non-empty name and description

**Then** a feature is persisted and displayed in the list and detail view.

### Scenario: Create and complete planned work

**Given** an existing feature

**When** a user creates a task and changes it to `DONE`

**Then** the task is linked to the feature and its progress reflects completion.

## Open questions

None for the implemented baseline.

## Traceability

- **Code:** `features/Feature.kt`, `FeatureService.kt`, `FeatureController.kt`,
  `tasks/Task.kt`, `TaskService.kt`, `TaskController.kt`, and JTE templates.
- **Tests:** `FeatureModuleTest`, `TaskModuleTest`, `FeatureControllerUiTest`,
  and `TaskControllerUiTest`.
- **ADRs:** None.
