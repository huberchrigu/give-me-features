# Use-case implementation assessment

This assessment compares [`use-cases.plantuml`](use-cases.plantuml) with the
current source tree. It is a static implementation review performed on
2026-09-18; the Gradle test suite could not be run in this environment because
Java is not configured.

## Contents

- [How to read the assessment](#how-to-read-the-assessment)
- [Implemented foundation](#implemented-foundation)
- [Feature use cases](#feature-use-cases)
- [Task use cases](#task-use-cases)
- [Interface, service, and solution-design use cases](#interface-service-and-solution-design-use-cases)
- [Test use cases](#test-use-cases)
- [Soft-feature use cases](#soft-feature-use-cases)
- [Cross-cutting observations](#cross-cutting-observations)
- [Recommended delivery order](#recommended-delivery-order)

## How to read the assessment

| Status | Meaning |
| --- | --- |
| Implemented | A user can complete the essential use case through a persisted domain model and UI/API path. |
| Partial | There is a useful technical or UI foundation, but the use case's business outcome is incomplete. |
| In progress | Worktree code establishes a foundation, but it is uncommitted and does not yet fulfill the complete use case. |
| Not implemented | No dedicated domain model, service, endpoint, or UI path was found. |

The diagram currently marks four use cases yellow as implemented: describing a
feature, implementing a feature, creating a task, marking a task blocked, and
completing a task. This is broadly correct for feature/task CRUD and task
status, but “implementing a feature” is better classified as partial: the
application plans and tracks tasks but has no explicit feature implementation
workflow.

## Implemented foundation

The following platform capabilities support several use cases without being
use cases themselves:

- Feature and task aggregates are persisted in MongoDB and use optimistic
  locking.
- Each aggregate has version history and a merge flow for concurrent edits.
- Feature and task pages use server-rendered JTE fragments, HTMX, and
  server-sent events to show live changes.
- A task can belong to one or more features; the UI supports creating a task
  from a feature and linking an existing task to a feature.
- The plugin API allows active plugins to add forms and change triggers to
  feature and task pages.

These are strong enablers, but they should not be mistaken for implementation
of reporting, collaboration, test execution, planning, or assignment workflows.

## Feature use cases

| Diagram use case | Status | Current implementation | Gap and recommended improvement |
| --- | --- | --- | --- |
| Describing a feature | Implemented | `Feature` has a required name and Markdown description. The feature list has a creation form; `FeatureController` persists it through `FeatureService`. Editing is supported. | Add acceptance criteria, priority, owner, lifecycle state, and links to the product context if these are needed for a complete requirement. |
| Implementing a feature | Partial | Tasks can be created under a feature, linked to it, and shown with their statuses. Feature progress is calculated as the percentage of linked tasks that are `DONE`. | Define a feature lifecycle and implementation plan explicitly. Add milestones, dependencies, delivery criteria, and a feature-level state instead of inferring progress solely from task status. |
| Fetching a feature report | Partial | The feature detail page shows linked tasks and calculated completion percentage. | Provide an actual report view/API: filterable portfolio list, task/status breakdown, age and blocked-work metrics, export, and a clear reporting date/snapshot. |
| Planning a feature go-live | Not implemented | No go-live/release entity, date, checklist, or endpoint was found. | Model a release/go-live plan with target environment/date, readiness checklist, approvals, rollback plan, and links to features. |
| Collaborating on a feature | Partial | SSE updates and version-history merge support concurrent editing and conflict recovery. | Add deliberate collaboration primitives: comments, mentions, activity/audit trail with actor and timestamp, subscriptions, and decision links. Live refresh is not a discussion workflow. |
| Testing a feature | In progress | The uncommitted `testing-plugin` adds a plugin form with a coarse test state and a reference to either a feature or task. | Finish and commit the plugin, then model named test cases, expected results, evidence, execution history, and a feature-level test summary. The current state field alone does not establish that testing occurred. |
| Completing a feature | Not implemented | A feature can display 100% calculated task progress, but it has no completion state or completion command. | Add an explicit completion transition, enforce agreed completion criteria, record who/when completed it, and decide how reopening and incomplete linked tasks behave. |

## Task use cases

| Diagram use case | Status | Current implementation | Gap and recommended improvement |
| --- | --- | --- | --- |
| Creating a task | Implemented | A task can be created from a feature page. It starts `OPEN` and is persisted before being linked to that feature. | The creation flow is feature-scoped. Add a standalone task inbox/backlog if tasks must be created before association with a feature. Capture description, estimate, priority, and ownership at creation as appropriate. |
| Adding progress to a task | Partial | A task's name and Markdown description can be edited, and its status can become `DONE`. | Add a progress model (percent, remaining work, or a small state machine) and dated progress updates. Do not rely on free-form description edits as progress tracking. |
| Marking a task as blocked | Implemented | `TaskStatus.BLOCKED`, `Task.block()`, `TaskService.blockTask`, and the task status menu implement the transition. | Record the blocker itself: reason, owner, dependency/link, date raised, and review/escalation information. |
| Tackling blocking issues | Not implemented | A blocked task can be reopened, but no blocking issue is represented or worked through. | Create a blocker/impediment entity or a first-class dependency workflow. It should have ownership, status, resolution, and backlinks to affected tasks. |
| Collaborating on a task | Partial | SSE, optimistic locking, history, and merge support concurrent task editing. | Add comments, mentions, assignments/notifications, and an activity timeline. Define whether collaborative edits need field-level permissions. |
| Completing a task | Implemented | `Task.close()` changes an `OPEN` or `BLOCKED` task to `DONE`; the controller exposes it in the status menu. Reopening is also supported. | Add completion metadata and, where relevant, validation of acceptance criteria, test evidence, and unresolved blockers before completion. |
| Assigning a task | Not implemented | `Task` contains name, description, and status only; no assignee or assignment endpoint/UI was found. | Add assignee/team ownership, assignment history, capacity/workload views, and notifications. This requires real user management first. |

## Interface, service, and solution-design use cases

| Diagram use case | Status | Current implementation | Gap and recommended improvement |
| --- | --- | --- | --- |
| Describing an interface | Not implemented | No interface aggregate, repository, controller, or template was found. | Define an interface catalogue with consumer/provider, protocol, contract version, owner, lifecycle, and links to features/tasks/tests. Consider OpenAPI or AsyncAPI attachments/imports. |
| Describing a service | Not implemented | No service aggregate, repository, controller, or template was found. | Add a service catalogue with ownership, purpose, dependencies, operational tier, interfaces, and links to solution designs. |
| Describing architecture | Not implemented | No solution-design aggregate or editing flow was found. The repository documentation contains PlantUML diagrams only. | Add versioned solution-design records with Markdown/diagram attachments, decisions, review/approval status, and links to services, interfaces, and features. |

## Test use cases

| Diagram use case | Status | Current implementation | Gap and recommended improvement |
| --- | --- | --- | --- |
| Describing a test | In progress | The uncommitted testing plugin has `TestDefinition`, but it only stores parent references and a state; it has no test name, steps, expected result, or specification. | Introduce a test-case aggregate with a stable identity, title, description/preconditions, steps, expected result, type, and lifecycle. |
| Linked to feature/task/interface | Partial | The plugin requires exactly one feature or task reference, so it can associate its record with one such parent. Interface linking is unavailable. | Allow explicit many-to-many links where required and add interface support only after interfaces exist. Avoid encoding a link as mutually exclusive fields once a test can cover multiple artifacts. |
| Marked as executable | Not implemented | No executability flag, environment, test run, or automation reference exists. | Add executable/manual classification, automation URI or command, required environment/data, and readiness validation. |
| Executing tests | Not implemented | The state can be entered as `RUNNING` or `FINISHED`, but no execution command, runner integration, result, evidence, or history exists. | Model immutable test runs with start/end, executor, environment, result, logs/artifacts, and retry history. Integrate an external runner only behind that domain boundary. |

## Soft-feature use cases

The diagram itself labels this package “To be defined.” None of the following
has a dedicated model or UI path; shared Markdown and live updates are only
technical prerequisites.

| Diagram use case | Status | Recommended improvement |
| --- | --- | --- |
| Starting a discussion | Not implemented | Add threaded discussion/comments with participants, mentions, and subscriptions. |
| Giving feedback | Not implemented | Add feedback records with source, target artifact, status, and resolution. |
| Tracking impediments | Not implemented | Reuse or extend the proposed blocker entity, including impact and ownership. |
| Making proposals/opinions | Not implemented | Add proposal records with alternatives, rationale, supporters, and outcome. |
| Tracking decisions | Not implemented | Add lightweight ADR/decision records with context, decision, consequences, owner, and links. |
| Recording conflicts | Not implemented | Add conflict records only if they have a defined workflow; otherwise represent them as discussions or unresolved decisions to avoid a redundant object type. |

## Cross-cutting observations

1. **Identity and permissions need to precede assignment and accountable
   workflows.** The application currently has development users (`user` and
   `admin`) and no user-management domain. A real identity/authorization model
   is needed before reliable ownership, approvals, notifications, or audit
   attribution can be delivered.

2. **Use explicit domain states for business outcomes.** Task status is a good
   start, but feature completion, go-live readiness, test execution, blocker
   resolution, and decision outcomes are not reliably derivable from text or a
   percentage. Each needs a state machine, transition rules, and history.

3. **Separate collaboration events from aggregate updates.** SSE and merge
   handle concurrent editing, but comments, feedback, and decisions need
   append-only records with actor, time, target, and notification semantics.

4. **Make plugin maturity visible.** The testing plugin is a useful extension
   seam, but is currently uncommitted. Its `TEXT` state field should become a
   typed select/enum control, as noted in the source TODO. Its persistence key
   should also be namespaced by parent type or otherwise guaranteed unique,
   since feature and task IDs are both stored as strings.

5. **Update the diagram as delivery progresses.** Keep yellow only for use
   cases meeting the “Implemented” definition above; mark partial work orange
   (or add a legend value for it). This will make the diagram a trustworthy
   product roadmap rather than a rough inventory.

## Recommended delivery order

1. Establish users, roles, audit attribution, and basic ownership. This unlocks
   assignment, approvals, and meaningful collaboration.
2. Complete the core delivery workflow: task progress/assignees/blockers,
   explicit feature lifecycle and completion criteria, then a feature report.
3. Finish the testing vertical slice: commit the plugin, add test cases and
   runs, link results to tasks/features, and use those results in completion
   criteria.
4. Add release/go-live planning so completed features can move into a governed
   delivery process.
5. Add service, interface, and solution-design catalogues, then connect them to
   features, tests, and decisions.
6. Introduce the soft-feature model only after agreeing its vocabulary and
   workflows; a single discussion/decision model may cover several of the
   currently separate labels.
