# Frontend Agent Guide

Instructions for AI agents working in this React app.

## Stack

- React 19 with React Compiler (enabled in Vite — do not add manual memoization by default)
- TypeScript (strict)
- Vite
- TanStack Router (file-based routes in `src/routes/`)
- TanStack Query (server state)
- Tailwind CSS v4 + shadcn/ui + Radix
- pnpm

## Agent behavior

- Make minimal diffs — only touch files required for the task
- Ask before refactors or structural changes
- Work in small, task-sized chunks
- Do not add dependencies, tests, or docs unless explicitly requested
- Do not add drive-by refactors or premature abstractions

## Project structure

```
src/
  features/           # feature modules
    <feature>/
      components/
      hooks/
      api/
  shared/             # code used by 2+ features
    components/
    hooks/
    api/
  components/
    ui/               # shadcn primitives only
  routes/             # thin TanStack Router files
  lib/                # shared utilities (e.g. cn())
```

### Placement rules

- **Feature-first** — components, hooks, and API logic belong in the owning `features/<name>/` folder
- **`shared/`** — only when used by **two or more features**
- **`components/ui/`** — shadcn/Radix primitives; extend via variants, do not duplicate
- **`routes/`** — thin route wiring only (see Routing)

## Routing (TanStack Router)

- Route files only define `Route` and import the page from `features/` or `shared/`
- Keep route files thin — no business logic or large components inline
- **Auth / route guards (future):** handle in the router layer (`beforeLoad`, loaders, route-level pending UI)
- **Do not** put auth-guard logic in React Query hooks

Example:

```tsx
// src/routes/dashboard.tsx
import { DashboardPage } from "@/features/dashboard/components/dashboard-page";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/dashboard")({
  component: DashboardPage,
});
```

## Loading & error handling

| Concern | Tool | Where |
|---------|------|-------|
| API / server state | TanStack Query | feature hooks + component UI |
| Auth / route guards | TanStack Router | `beforeLoad`, loaders, `pendingComponent` |
| Navigation errors | TanStack Router | route `errorComponent` |

### Server data (React Query)

- All API calls go through TanStack Query hooks in `features/*/hooks/` or `shared/hooks/`
- Components consume `isPending`, `isError`, and `error` from those hooks
- Do not call `fetch` directly in components

### Mock API (current phase)

- Mock responses live in `features/<name>/api/` until Hey API is integrated
- Shape mocks to match future API contracts (`interface` + Zod where useful)

## React patterns

### Memoization

React Compiler is enabled. **Do not** add `useMemo`, `useCallback`, or `memo` unless:

- There is a proven performance issue, or
- A third-party API requires a stable reference

### Components

- **One component per file**
- **Named exports only** — `export function UserProfile()`, never default exports
- Functional components only

```tsx
// good
export function UserProfile() {
  return <div>...</div>;
}

// bad
export default function UserProfile() { ... }
```

### Custom hooks

Extract a hook when:

- Logic is reused in two or more places, or
- A component is complex enough that extraction improves readability

Place hooks in the owning feature: `features/<name>/hooks/`

## Exports & imports

- **No barrel exports** — no `index.ts` re-export files; import directly from the source file
- **Named exports only** in every file
- **Imports:** relative paths within the same feature; `@/` alias when crossing features or importing from `shared/`

```tsx
// within a feature
import { UserCard } from "./user-card";

// cross-feature or shared
import { Button } from "@/components/ui/button";
import { formatDate } from "@/shared/lib/format-date";
```

## UI & styling

- Use shadcn components from `components/ui/` — extend via CVA variants, do not rebuild primitives
- Use `cn()` from `@/lib/utils` for className merging
- Tailwind utility classes only — avoid inline `style={{}}` except for truly dynamic values

## TypeScript

- Strict mode — no `any`
- Prefer `interface` over `type` for object shapes
- Use `type` for unions, utilities, and mapped types
- Explicit prop types on exported components
- Zod for runtime validation at API boundaries (when applicable)

## Naming

| Kind | Convention | Example |
|------|------------|---------|
| Files | kebab-case | `user-profile.tsx` |
| Components | PascalCase | `UserProfile` |
| Hooks | camelCase with `use` prefix | `useUserProfile` |
| Folders | kebab-case | `features/user-profile/` |

## Testing

Do not add tests unless explicitly requested.
