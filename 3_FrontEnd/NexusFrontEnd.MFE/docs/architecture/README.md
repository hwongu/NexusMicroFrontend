# NexusFrontEnd.MFE Architecture

## Original architecture detected in NexusFrontEnd

The source project is an Angular 19 standalone application using Angular Material. It originally grouped cross-cutting concerns under `core` and business screens under `features`.

Original domains:
- auth
- dashboard
- categories
- users
- products
- revenues

Original behavior preserved:
- login backed by `localStorage`
- auth guard using `current_user` and `session_expiration`
- same route semantics
- same backend endpoints and environments
- same Angular Material visual base

## New architecture

The workspace now uses a thin host plus business remotes.

Host responsibilities:
- global bootstrap
- top-level routing
- main layout
- auth bootstrap
- auth guard wiring
- global navigation
- remote loading
- remote fallback view
- global error handler
- analytics placeholder
- permission placeholder

Business remotes:
- `mfe-auth`
- `mfe-dashboard`
- `mfe-categories`
- `mfe-users`
- `mfe-products`
- `mfe-revenues`

## What stayed in the host

Host source now lives only around transversal concerns:
- `src/app/app.component.*`
- `src/app/app.config.ts`
- `src/app/app.routes.ts`
- `src/app/core/layout/*`
- `src/app/shell/remote-unavailable.component.ts`

The host no longer contains business feature folders or local business services.

## What moved into each micro frontend

### mfe-auth
- login component
- auth route `/login`
- uses shared auth service

### mfe-dashboard
- dashboard screen
- route `/dashboard`
- uses shared auth service for session-based greeting behavior

### mfe-categories
- categories list
- categories create form
- categories edit form
- local categories service
- routes `/categories`, `/categories/new`, `/categories/edit/:id`

### mfe-users
- users list
- users create form
- users edit form
- local users service
- routes `/users`, `/users/new`, `/users/edit/:id`

### mfe-products
- products list
- products create form
- products edit form
- local products service
- local categories lookup service
- routes `/products`, `/products/new`, `/products/edit/:id`

### mfe-revenues
- revenues list
- revenue details inside the list flow
- revenue creation form
- local revenues service
- local products lookup service
- local users lookup service
- routes `/revenues`, `/revenues/new`

## Shared code

Only transversal code remains in `src/shared/core`:
- `auth/auth.service.ts`
- `auth/permissions.service.ts`
- `auth/session-bootstrap.service.ts`
- `guards/auth.guard.ts`
- `analytics/analytics.service.ts`
- `errors/global-error-handler.ts`
- `constants/api-endpoints.ts`
- `models/*`

What was intentionally not kept in shared:
- category business service
- user business service
- product business service
- revenue business service

Those now live in their own remotes to avoid recreating the monolith inside shared.

## Routing resolution

Host routes:
- `/login` -> `mfe-auth`
- `/dashboard` -> `mfe-dashboard`
- `/categories/**` -> `mfe-categories`
- `/users/**` -> `mfe-users`
- `/products/**` -> `mfe-products`
- `/revenues/**` -> `mfe-revenues`

Each remote owns its internal domain routes and exposes route definitions, not a single loose component.

## Federation configuration

Manifest file:
- `public/federation.manifest.json`

Remote federation configs:
- `projects/mfe-auth/federation.config.js`
- `projects/mfe-dashboard/federation.config.js`
- `projects/mfe-categories/federation.config.js`
- `projects/mfe-users/federation.config.js`
- `projects/mfe-products/federation.config.js`
- `projects/mfe-revenues/federation.config.js`

Manifest entries:
- `mfe-auth` -> `http://localhost:4201/remoteEntry.json`
- `mfe-dashboard` -> `http://localhost:4202/remoteEntry.json`
- `mfe-categories` -> `http://localhost:4203/remoteEntry.json`
- `mfe-users` -> `http://localhost:4204/remoteEntry.json`
- `mfe-products` -> `http://localhost:4205/remoteEntry.json`
- `mfe-revenues` -> `http://localhost:4206/remoteEntry.json`

## Auth resolution

Authentication remains centralized through shared auth utilities:
- `AuthService` stays transversal
- `authGuard` stays transversal
- session bootstrap runs at host startup
- login screen lives in `mfe-auth`
- post-login navigation still lands on `/dashboard`

## Cross-domain dependencies

The host does not orchestrate business dependencies.

Instead:
- `mfe-products` resolves category lookups locally through its own local category service
- `mfe-revenues` resolves users and products through its own local lookup services
- shared code only provides models, endpoint constants, auth, and transversal infrastructure

This keeps the host thin while still allowing the domains to communicate with the existing backend.

## Remote loading and errors

The host uses federation route loading for each remote.
If a remote cannot be loaded, the host falls back to a transversal `RemoteUnavailableComponent` instead of crashing the whole shell.

A global error handler is also registered in the host and forwards errors to the analytics placeholder.

## Analytics placeholder

A lightweight `AnalyticsService` exists in shared code to provide a single future integration point for telemetry.
Current behavior is intentionally minimal and console-based.

## Ports

- shell: `4200`
- mfe-auth: `4201`
- mfe-dashboard: `4202`
- mfe-categories: `4203`
- mfe-users: `4204`
- mfe-products: `4205`
- mfe-revenues: `4206`

## Scripts

Start scripts:
- `npm run start:shell`
- `npm run start:auth`
- `npm run start:dashboard`
- `npm run start:categories`
- `npm run start:users`
- `npm run start:products`
- `npm run start:revenues`

Build scripts:
- `npm run build:shell`
- `npm run build:auth`
- `npm run build:dashboard`
- `npm run build:categories`
- `npm run build:users`
- `npm run build:products`
- `npm run build:revenues`

## How to run one app

Run the script for the app you want. Examples:
- only host: `npm run start:shell`
- only users remote: `npm run start:users`
- only products remote: `npm run start:products`

A remote can start on its own port, but the full user flow requires the host plus the matching remotes.

## How to run the host with all remotes

Open one terminal per app inside `NexusFrontEnd.MFE` and run:
- `npm run start:shell`
- `npm run start:auth`
- `npm run start:dashboard`
- `npm run start:categories`
- `npm run start:users`
- `npm run start:products`
- `npm run start:revenues`

Then open `http://localhost:4200`.

## What was copied directly

Mostly copied from the original project and adapted to remote boundaries:
- login feature
- dashboard feature
- categories feature
- users feature
- products feature
- revenues feature
- layout base
- models
- endpoint constants
- auth behavior

## What was adapted

- host routing now loads remotes instead of components
- business services moved out of host/shared into remotes
- shell gained session bootstrap, analytics placeholder, global error handling, and remote fallback handling
- dashboard, auth, categories, users, products, and revenues were converted into remote-owned routes
- products and revenues now use local lookup services inside their own remotes to avoid pushing business coordination into the host
- explicit development ports and scripts were aligned across all apps

## Verified state

Verified by build:
- host build
- auth remote build
- dashboard remote build
- categories remote build
- users remote build
- products remote build
- revenues remote build

Verified by inspection:
- host manifest entries
- remote federation configs
- top-level host routes
- per-remote internal route exports
- package scripts
- Angular port configuration
- host source trimmed to transversal concerns

## Risks and tradeoffs

- The same backend contracts are still shared across domains, so backend coupling still exists even though frontend ownership is separated.
- Products and revenues remain the most coupled domains because they need lookup data from other business areas.
- The federation build emits a warning about wildcard path mappings, but all apps compile successfully.
- Local development still requires multiple processes until an orchestration script is added.
- Runtime smoke testing in a browser was not automated in this pass; verification here is based on configuration review and successful builds.

## Start-all BAT

A root-level batch file was added:
- `start-all.bat`

How to use it:
- double click `start-all.bat`, or
- run `start-all.bat` from the root of `NexusFrontEnd.MFE`

What it launches:
- `npm run start:shell`
- `npm run start:auth`
- `npm run start:dashboard`
- `npm run start:categories`
- `npm run start:users`
- `npm run start:products`
- `npm run start:revenues`

Batch window mapping:
- `Nexus Shell (4200)`
- `Nexus Auth (4201)`
- `Nexus Dashboard (4202)`
- `Nexus Categories (4203)`
- `Nexus Users (4204)`
- `Nexus Products (4205)`
- `Nexus Revenues (4206)`

Recommended startup flow:
1. Run `start-all.bat`
2. Wait until the remotes finish compiling
3. Open `http://localhost:4200`

How to run a single app manually:
- shell: `npm run start:shell`
- auth: `npm run start:auth`
- dashboard: `npm run start:dashboard`
- categories: `npm run start:categories`
- users: `npm run start:users`
- products: `npm run start:products`
- revenues: `npm run start:revenues`

How to diagnose if a remote does not respond:
- check the command window opened for that remote
- confirm its assigned port is free
- confirm the host manifest still points to the expected remoteEntry URL
- the host will show the remote fallback view when a remote cannot be loaded

How to compile manually:
- shell: `npm run build:shell`
- auth: `npm run build:auth`
- dashboard: `npm run build:dashboard`
- categories: `npm run build:categories`
- users: `npm run build:users`
- products: `npm run build:products`
- revenues: `npm run build:revenues`

## Stop-all BAT

A root-level batch file was added:
- `stop-all.bat`

How to use it:
- double click `stop-all.bat`, or
- run `stop-all.bat` from the root of `NexusFrontEnd.MFE`

What it does:
- closes the development windows started by `start-all.bat`
- kills the matching process tree for each titled window

Window titles it targets:
- `Nexus Shell (4200)`
- `Nexus Auth (4201)`
- `Nexus Dashboard (4202)`
- `Nexus Categories (4203)`
- `Nexus Users (4204)`
- `Nexus Products (4205)`
- `Nexus Revenues (4206)`

Recommended shutdown flow:
1. Run `stop-all.bat`
2. Confirm each window reports as closed or already absent
3. If a process remains, close its console manually

## Centralized environments

The workspace now uses the root environment files again, like the monolith did.

Root files:
- `src/environments/environment.ts`
- `src/environments/environment.development.ts`
- `src/environments/environment.prod.ts`

How it works:
- the shell reads the root environment files directly
- each remote resolves `@env/environment` back to `src/environments/*`
- each remote includes the root environment files in its TypeScript compilation
- each remote build now replaces the root environment file for `development` and `production`

Why this matters:
- we avoid runtime provider issues introduced by per-remote environment injection
- `start-all.bat` goes back to the centralized configuration model you were using before
- development and production behavior stay aligned across the shell and all remotes
