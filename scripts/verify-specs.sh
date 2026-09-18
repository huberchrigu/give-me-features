#!/usr/bin/env bash
set -euo pipefail

status=0
specs=()
while IFS= read -r file; do specs+=("$file"); done < <(
  find docs/specs -maxdepth 1 -type f -regextype posix-extended \
    -regex '.*/[A-Z]+-[0-9]+-.*\.md' | sort
)

if [ "${#specs[@]}" -eq 0 ]; then
  echo "No numbered specifications found in docs/specs."
  exit 1
fi

for spec in "${specs[@]}"; do
  for required in '^# ' '^- \*\*ID:\*\* [A-Z]+-[0-9]+' '^- \*\*Status:\*\* (Draft|Approved|Implemented|Superseded)$' '^## Goal$' '^## Scope$' '^## Domain rules$' '^## Acceptance scenarios$' '^## Open questions$' '^## Traceability$' '^- \*\*Code:\*\*' '^- \*\*Tests:\*\*' '^- \*\*ADRs:\*\*'; do
    if ! grep -Eq "$required" "$spec"; then
      echo "${spec}: missing required content matching ${required}"
      status=1
    fi
  done
done

exit "$status"
