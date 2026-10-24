# Profile Link

A small Spring Boot application for creating shareable public profiles.

Each profile contains a name, headline, and list of skills. When a profile is created, the application generates a unique shareable ID and public URL that can later be used to retrieve and display the profile.

## Features

- Create a profile with:
  - Name
  - Headline
  - Skills
- Generate a unique 16-character shareable profile ID
- View profiles through a public URL
- JSON-based local persistence
- Cache-aside lookup using a local JSON cache
- Input validation and normalization
- Graceful handling of missing profiles
- Structured validation error responses
- Lightweight health check endpoint
- Cache hit/miss debug logging
- Responsive web interface
- Unit and validation tests

## Screenshots

### Create a Profile

![Create Profile](/docs/images/main.png)

### Shareable Link Generated

![Profile Created](/docs/images/created.png)

### Public Profile

![Public Profile](/docs/images/profile.png)

### Profile Not Found

![Profile Not Found](/docs/images/not-found.png)

## Tech Stack

- Java 21
- Spring Boot 4.1
- Spring Web MVC
- Jakarta Validation
- Jackson
- HTML
- CSS
- Vanilla JavaScript
- JUnit 6

No external database, authentication system, frontend framework, or deployment infrastructure is required.

## Running the Application

### Prerequisite

Java 21 must be installed and available through `JAVA_HOME`.

Verify with:

```bash
java -version
```

### Run

On Windows:

```powershell
.\mvnw spring-boot:run
```

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

Then open:

```text
http://localhost:8080
```

## API

### Create a profile

```http
POST /api/profiles
```

Example request:

```json
{
  "name": "Turgut Barış Satır",
  "headline": "Software Engineer",
  "skills": [
    "Java",
    "Spring Boot",
    "REST APIs"
  ]
}
```

Example response:

```json
{
  "id": "d41f7b49c2ac43d0",
  "name": "Turgut Barış Satır",
  "headline": "Software Engineer",
  "skills": [
    "Java",
    "Spring Boot",
    "REST APIs"
  ]
}
```

### Retrieve a profile

```http
GET /api/profiles/{id}
```

If the profile exists, the API returns the profile with `200 OK`.

If the ID does not exist, the API returns `404 Not Found`.

### Health check

```http
GET /api/health
```

Example response:

```json
{
  "status": "UP"
}
```

The endpoint provides a lightweight way to verify that the application is running and reachable.

## Shareable Profiles

Profiles can also be viewed through a human-readable public page:

```text
http://localhost:8080/p/{id}
```

The creation page generates this URL automatically and provides a copy-to-clipboard action.

## Persistence

Profiles are stored locally in:

```text
profiles.json
```

The file acts as the application's local source of truth and allows profiles to survive application restarts.

Runtime data files are excluded from Git.

## Cache

The application also includes a small cache abstraction backed by:

```text
cache.json
```

Profile lookup follows a cache-aside flow:

```text
Request
   ↓
Cache
   ↓ miss
Persistent repository
   ↓
Cache population
   ↓
Response
```

Cache failures are non-fatal. If the cache cannot be read or written, the application falls back to the persistent profile repository.

The local cache is intentionally simple. Its purpose is to demonstrate the boundary and access pattern that could later be implemented using a shared cache such as Redis.


Cache activity is logged at debug level so cache hits, misses, repository fallback, and cache population can be observed while running the application.

Cache failures remain non-fatal: the repository is treated as the source of truth.


## Validation

The API validates incoming profiles before creation.

Current rules include:

- Name is required and limited to 100 characters
- Headline is required and limited to 160 characters
- At least one skill is required
- Maximum of 20 skills
- Individual skills cannot be blank
- Individual skills are limited to 50 characters
- Leading and trailing whitespace is removed
- Duplicate skills are removed


Validation failures are returned as structured `400 Bad Request` responses.

Example:

```json
{
  "error": "Validation failed",
  "fields": {
    "name": "Name is required"
  }
}
```

This keeps client-side error handling predictable and avoids exposing the default framework error format.

## Product Decisions

### Required vs. Optional Fields

All three core profile fields are required:

- `name`
- `headline`
- `skills`

I intentionally did not introduce optional profile fields because the project is deliberately small, and adding optional metadata would not materially improve the main share-and-retrieve flow.

Invalid or missing required values are rejected with validation errors before a profile is created.

There are currently no optional profile fields, so there is no special fallback behavior for missing optional data.

### Repeated Profile Creation

A new profile creation request always generates a new profile and a new shareable ID, even if its contents match an existing profile.

This keeps creation behavior explicit and avoids adding profile deduplication logic that is outside the core scope of the project.

### Shareable ID

Each profile receives a randomly generated 16-character identifier derived from a UUID.

Before a profile is persisted, the application checks whether the generated ID already exists. In the unlikely event of a collision, a new ID is generated.

The ID is intentionally non-sequential and difficult to guess compared with a simple numeric identifier.

## Tests

Run the test suite with:

```bash
./mvnw clean test
```

On Windows:

```powershell
.\mvnw clean test
```

The current tests cover:

- Application context startup
- Profile creation
- Persistence and cache interaction
- Cache-first retrieval
- Repository fallback on cache miss
- Missing profile handling
- Input normalization
- Request validation

## Project Structure

```text
src/main/java/io/github/tbarissatir/profilelink
├── cache
├── controller
├── dto
├── exception
├── model
├── repository
├── service
└── ProfileLinkApplication.java

src/main/resources
└── static
    ├── index.html
    ├── profile.html
    ├── app.js
    ├── profile.js
    ├── styles.css
    └── favicon.ico
```

## Scope

The project deliberately keeps infrastructure small.

Authentication, a production database, deployment, and a frontend framework were intentionally not added because they were outside the intended scope.

Additional design decisions and production considerations are documented in `DESIGN.md`.
