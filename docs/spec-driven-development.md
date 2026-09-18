# Spec-driven development

This repository treats a specification as the durable agreement for a product
capability. Code, tests, and operational documentation implement and verify
that agreement; they do not replace it.

## Workflow

1. Create a spec in [`docs/specs`](specs) before starting a material capability
   or behavior change.
2. Keep it **Draft** while scope, rules, acceptance scenarios, and open
   decisions are resolved. An approved spec is the implementation input.
3. Record consequential cross-cutting decisions as ADRs in `docs/adr` and link
   them from the spec.
4. Implement the smallest coherent vertical slice and keep links to code and
   automated tests current.
5. Mark a spec **Implemented** only after scenarios are automated or have a
   documented, justified manual verification.
6. Mark a replaced spec **Superseded** rather than rewriting historical intent.

## Rules

- One spec describes one capability or tightly coupled vertical slice.
- State non-goals and unresolved decisions explicitly.
- Express observable behavior as acceptance scenarios, preferably Given/When/Then.
- Model business state and allowed transitions explicitly.
- Keep code, test, and ADR links current in **Traceability**.
- Update [`use-case-implementation-assessment.md`](use-case-implementation-assessment.md)
  when delivery status changes.

Run `bash scripts/verify-specs.sh` locally. CI runs the same structural check;
it is a safety net, not a substitute for review.
