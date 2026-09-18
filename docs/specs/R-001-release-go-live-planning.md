# Release and feature go-live planning

- **ID:** R-001
- **Status:** Draft
- **Use cases:** Planning a feature go-live
- **Owner:** Product Owner and Software Architect

## Goal

Provide an auditable plan for moving ready features into production.

## Scope

Add go-live plans, feature associations, readiness checks, approvals, rollback
plans, and recorded outcomes. Deployment automation and environment provisioning
are excluded.

## Domain rules

- A plan has target environment/date, owner, linked features, readiness checks,
  approvals, rollback plan, and outcome.
- A plan cannot become ready until required checks and approvals pass; overrides
  record reason and actor.
- Completion, abort, and rollback retain the original plan history.

## Acceptance scenarios

### Scenario: Plan a feature go-live

**Given** a feature is ready for release

**When** an authorized user creates a go-live plan and readiness items

**Then** it links to the feature and exposes incomplete checks and approvals.

## Open questions

- Which approval roles and checks are mandatory?
- What distinguishes a release, deployment, and go-live?
- Which environments are in scope?

## Traceability

- **Code:** Planned release module, feature integration, security, and UI.
- **Tests:** Planned lifecycle, authorization, and end-to-end plan tests.
- **ADRs:** Planned release-state and approval-model ADRs.
