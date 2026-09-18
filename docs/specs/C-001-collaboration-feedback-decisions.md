# Collaboration, feedback, and decisions

- **ID:** C-001
- **Status:** Draft
- **Use cases:** Collaboration, discussions, feedback, impediments, proposals, decisions, conflicts
- **Owner:** Requirements Engineer and Software Architect

## Goal

Capture human context behind delivery work without confusing concurrent editing
infrastructure with collaboration itself.

## Scope

Add attributed append-only discussions, feedback, proposals, decisions, and
impediments targeted at delivery artifacts. Chat replacement and real-time
presence are excluded.

## Domain rules

- Every record has target, author, timestamps, status, and audit history.
- Discussions support replies and mentions; feedback has a resolution state.
- Decisions record context, alternatives, outcome, owner, and consequences.
- Impediments reuse T-001 blockers where semantics match.

## Acceptance scenarios

### Scenario: Resolve feature feedback

**Given** a feature with open feedback

**When** an authorized participant resolves it with rationale

**Then** its history shows author, resolver, rationale, and resolution time.

## Open questions

- Which roles may resolve feedback or make decisions?
- What notification and retention policy is required?
- Do ADR files remain canonical for architecture decisions?

## Traceability

- **Code:** SSE/history are prerequisites; collaboration aggregates, views, and notifications are planned.
- **Tests:** Planned authorization, audit-history, and UI tests.
- **ADRs:** Planned collaboration event-model ADR.
