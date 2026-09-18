---
name: feature-spec
description: Create or revise a repository capability specification when a product behavior or use case needs agreement before implementation.
---

Create or update a numbered specification in `docs/specs` using
`docs/specs/_template.md` and follow `docs/spec-driven-development.md`.

Read the relevant existing spec, the use-case implementation assessment, and
only the code needed to establish current behavior. Preserve implemented facts;
distinguish them from desired behavior and put unresolved decisions in Open
questions. Do not invent product policy, roles, or state transitions.

Make scope and non-goals explicit. Add observable acceptance scenarios and
traceability links. For new or changed behavior, retain Draft status unless the
user supplies approval. Update the use-case assessment when delivery status
changes, then run `bash scripts/verify-specs.sh`.
