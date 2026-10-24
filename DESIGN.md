# Design Decisions

This document explains the main technical and product decisions behind Profile Link.

## 1. Technology Choice

The application is implemented with Java 21 and Spring Boot 4.1.

Spring Boot was chosen because it provides a clean structure for separating HTTP, business logic, persistence, validation, and error handling without requiring unnecessary infrastructure. It also keeps the application easy to run locally with the Maven wrapper.

The frontend uses plain HTML, CSS, and JavaScript. A frontend framework was intentionally avoided because the UI is small and does not require complex client-side state management.

## 2. Application Structure

The application follows a small layered structure:

```text
Controller
    ↓
Service
    ↓
Cache / Repository
```

Responsibilities are separated as follows:

- `controller` handles HTTP requests and page routing.
- `service` contains profile creation and retrieval logic.
- `repository` owns persistent profile storage.
- `cache` provides a separate cache abstraction.
- `dto` defines incoming request data.
- `model` represents a profile.
- `exception` contains application-specific error handling.

The goal is to keep responsibilities clear without introducing unnecessary architectural layers for a small project.

## 3. Required and Optional Profile Fields

The three core fields are treated as required:

- `name`
- `headline`
- `skills`

No optional profile fields were added.

This was a deliberate scope decision. The create-and-share flow is fully represented by these fields, and adding optional metadata would increase surface area without materially improving the core functionality.

Invalid or missing required fields are rejected before profile creation.

Current validation rules are:

- Name: required, maximum 100 characters
- Headline: required, maximum 160 characters
- Skills: at least one and maximum 20
- Each skill: non-blank and maximum 50 characters

Before persistence, leading and trailing whitespace is removed and duplicate skills are removed.

## 4. Shareable ID Design

Each profile receives a 16-character hexadecimal identifier derived from a randomly generated UUID.

Example:

```text
1cecda5a34b4e97f
```

The ID is:

- non-sequential
- compact enough for sharing
- considerably harder to guess than a numeric or short sequential identifier

Before saving a profile, the repository is checked for an existing matching ID. If a collision occurs, another ID is generated.

The ID is used as a public identifier, not as an authentication secret. In a production system, authorization requirements would need to be considered separately if profile visibility became restricted.

## 5. Repeated Profile Creation

Creating the same profile data multiple times generates a new profile and a new shareable ID each time.

The application does not attempt to determine whether two profile payloads represent the same logical person.

This avoids introducing ambiguous deduplication rules such as:

- whether name matching is case-sensitive
- whether skills must match exactly
- whether an edited headline represents the same profile
- whether existing links should be mutated or reused

In this project, a POST request represents creation of a new resource.

## 6. Persistence

Profiles are persisted to a local `profiles.json` file.

The file is treated as the source of truth and allows profiles to survive application restarts while keeping the project free from external database setup.

The repository abstraction keeps persistence concerns outside the service layer, so the JSON-backed implementation could later be replaced with a database-backed repository without changing the controller contract.

The runtime data file is excluded from Git.

### Trade-off

The current JSON implementation is appropriate for a small local project, but it is not intended as production-grade persistence.

Possible production concerns include:

- concurrent writes
- atomic file replacement
- process crashes during writes
- multiple application instances
- large data volume
- querying and indexing

A production version would replace the JSON repository with a persistent database.

## 7. Cache Design

The application includes a `ProfileCache` abstraction with a local JSON-backed implementation.

Profile retrieval follows a cache-aside pattern:

```text
GET profile
    ↓
Check cache
    ↓
Cache hit ─────→ return profile
    ↓ miss
Read repository
    ↓
Populate cache
    ↓
Return profile
```

The cache is deliberately treated as disposable infrastructure.

If a cache read or write fails, the application logs the failure and continues using the persistent repository. A cache problem must not prevent a valid profile from being created or retrieved.

Cache hits, misses, repository fallback, and cache population are also logged at debug level. This makes the cache-aside behavior observable during local development without changing the API response contract.

If the cache file itself is corrupted, the application starts with an empty cache rather than failing application startup.

### Why use a cache here?

For the current implementation, the JSON repository already loads its data into memory, so the local cache does not provide a meaningful performance improvement.

It is included to demonstrate a clean cache boundary and cache-aside access pattern without introducing external infrastructure.

In a production system, the same abstraction could be implemented using Redis or another shared cache, potentially with:

- TTL-based expiration
- shared state across application instances
- explicit invalidation
- metrics and observability

## 8. Error Handling

Requests for unknown profile IDs return `404 Not Found` instead of returning an empty response or generic server error.

Validation failures are rejected before business logic executes.

Validation errors are returned in a structured `400 Bad Request` response containing an application-level error message and field-specific validation messages. This gives API clients a predictable contract instead of relying on the framework's default error payload.

Cache failures are non-fatal because the repository is the source of truth.

Persistent repository failures are treated differently: if the source of truth cannot be read or written reliably, the application should fail rather than silently pretending the operation succeeded.

## 9. Health Check

The application exposes a lightweight health endpoint:

```http
GET /api/health
```

It returns:

```json
{
  "status": "UP"
}
```

A custom endpoint was preferred over adding Spring Boot Actuator because the application only needs a minimal liveness-style check. This keeps the dependency surface small while still providing an explicit operational signal.

## 10. Frontend Design

The frontend is intentionally lightweight and implemented with static HTML, CSS, and vanilla JavaScript.

It provides:

- profile creation form
- generated shareable link
- copy-to-clipboard action
- public profile page
- missing-profile state
- responsive layout

User-supplied profile values are inserted into the page using text-based DOM APIs rather than raw HTML injection.

A larger frontend framework was not introduced because the application does not have enough client-side complexity to justify it.

## 11. Testing Strategy

The current automated tests focus on the most important application behavior:

- application context startup
- profile creation
- cache population
- cache-first retrieval
- repository fallback on cache miss
- missing profile handling
- input normalization
- request validation

The goal was targeted coverage of the core flow rather than a large test suite.

For a production system, additional tests would include:

- HTTP/controller integration tests
- concurrent persistence tests
- malformed persistence-file scenarios
- cache failure scenarios
- end-to-end browser tests

## 12. Intentionally Out of Scope

The following were intentionally not added:

- authentication or login
- authorization
- production database
- Redis or other external cache infrastructure
- deployment configuration
- frontend framework
- full end-to-end test suite
- profile editing or deletion

These features were outside the intended scope and would add complexity without improving the core create, share, and retrieve workflow.

## 13. Production Evolution

A production-oriented version could evolve toward:

```text
Browser / Client
       ↓
Spring Boot API
       ↓
Service Layer
   ↙         ↘
Redis      Database
Cache      Source of Truth
```

Likely improvements would include:

- relational or document database persistence
- Redis-based distributed caching
- atomic and transactional writes
- concurrency-safe persistence
- richer operational health checks
- centralized observability and metrics
- configurable storage and cache settings
- authentication and authorization if profile visibility requires it
- rate limiting if the API becomes publicly exposed

The current implementation keeps these concerns behind simple interfaces where practical, while remaining small enough to review and run easily.
