# AI Skills — Context & Decision Guide

## Quick Decision Table

| Task type | Primary skill | Secondary skill |
|---|---|---|
| Create new feature end-to-end | `feature-generator` | `compose-architecture`, `api-client`, `compose-state` |
| Decide where code belongs | `compose-architecture` | — |
| Design ViewModel state / events | `compose-state` | — |
| Generate API service / DTO / Mapper | `api-client` | `compose-architecture` |
| Handle errors across layers | `error-handling` | `compose-state` |
| Set up navigation events in ViewModel | `navigation-flow` | `android-skills/navigation-3` |
| Implement Navigation 3 (NavHost, deep links, back stack) | `android-skills/navigation-3` | `navigation-flow` |
| Edge-to-edge / insets / IME | `android-skills/edge-to-edge` | — |
| AGP / build config upgrade | `android-skills/agp-upgrade` | — |
| Migrate XML Views → Compose | `android-skills/migrate-xml-to-compose` | — |
| R8 / ProGuard analysis | `android-skills/r8-analyzer` | — |
| Pre-commit / pre-PR review | `project-rules` | — |

## Skill Priority Order

```
1. project-rules        ← ALWAYS apply checklist before generating code
2. android-skills       ← Source of truth for platform patterns (do NOT override)
3. compose-architecture ← Source of truth for layer boundaries
4. feature-generator    ← Orchestrates all skills for full feature generation
5. compose-state        ← ViewModel state / event contracts
6. api-client           ← API code generation
7. error-handling       ← Error flow conventions
8. navigation-flow      ← Project-specific nav event pattern (wraps android-skills)
```

## Conflict Resolution Rules

- If **android-skills** and a project skill define the same pattern → **android-skills wins**.
- `navigation-flow` defines _project-specific_ event patterns only. For Navigation 3 mechanics (NavHost, NavKey, back stack) → use `android-skills/navigation-3`.
- `compose-state` defines _project-specific_ state shape. For Compose lifecycle/state hoisting best practices → android-skills apply.
- When rules overlap: the **more specific** and **more recent** skill wins.

## Skill Ownership

| Type | Owner | Can edit? |
|---|---|---|
| `.ai/android-skills/` | Google LLC | ❌ NO |
| `.ai/project-rules/`, `.ai/compose-*`, `.ai/api-client/`, `.ai/error-handling/`, `.ai/feature-generator/`, `.ai/navigation-flow/` | This project | ✅ YES |