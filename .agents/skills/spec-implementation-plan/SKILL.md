---
name: spec-implementation-plan
description: Turn an approved repository specification into a bounded implementation plan before changing application code.
---

Read the target specification, its linked ADRs, and relevant code/tests. Confirm
it is Approved, or ask whether planning a Draft is intended; never treat a Draft
as authorization to implement.

Map every acceptance scenario to affected modules, aggregates, persistence and
history, controllers/templates, authorization, and automated tests. Identify
migrations, compatibility concerns, and product decisions that block safe work.
Keep the plan within the specification scope. Propose an ADR for consequential
cross-cutting choices instead of deciding them implicitly.
