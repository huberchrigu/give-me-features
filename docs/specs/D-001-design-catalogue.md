# Service, interface, and solution-design catalogue

- **ID:** D-001
- **Status:** Draft
- **Use cases:** Describing interfaces, services, and architecture
- **Owner:** Software Architect

## Goal

Provide a versioned catalogue connecting services, interfaces, and solution
designs to delivery work.

## Scope

Add service, interface, and solution-design records with ownership, lifecycle,
relationships, and links to features/tasks/tests. Runtime discovery and automatic
diagram generation are excluded.

## Domain rules

- A service has purpose, owner, dependencies, operational tier, and lifecycle.
- An interface has provider, consumers, protocol, contract/version, owner,
  lifecycle, and reference material.
- A solution design is versioned with rationale, diagrams/attachments,
  decisions, and review status.
- Relationship links retain referential integrity and history.

## Acceptance scenarios

### Scenario: Describe a service and interface

**Given** an architect

**When** they create a service and interface contract

**Then** the catalogue shows ownership, lifecycle, and their relationship.

## Open questions

- Is this catalogue native or synchronized from an existing system?
- Which diagram and contract formats need first-class support?
- What review process applies to solution designs?

## Traceability

- **Code:** Existing `epics` are unrelated persistence only; catalogue modules and views are planned.
- **Tests:** Planned aggregate/history, relationship, and UI tests.
- **ADRs:** Planned catalogue ownership and document-storage ADRs.
