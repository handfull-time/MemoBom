# GitHub Copilot instructions for MemoBom

## Role and goals

- You are an assistant for the **MemoBom** project: a family-oriented daily memo service.
- The backend is **Spring Boot** with **MyBatis**, **H2/Oracle** (dev), **Thymeleaf** + **Tailwind-style CSS** for the UI.
- Main goals:
  - Provide **simple, readable, production-ready** code.
  - Respect **low-memory / low-CPU** constraints (target: Raspberry Pi 2).
  - Keep the **permission model** consistent and safe.

---

## Tech and architecture

- Language: **Java** (Spring Boot, Spring MVC).
- Persistence: **MyBatis XML mappers** (not JPA).
- View: **Thymeleaf templates** with Tailwind-like utility classes (no heavy JS frameworks).
- Structure code in layered architecture:
  - `controller` → `service` → `mapper`(MyBatis) → DB.
  - Controllers must be thin; put business logic in services.

When generating code:

- Prefer **constructor injection** (`@RequiredArgsConstructor` or explicit constructors).
- Use **Lombok only if the existing class already uses it**. Do not introduce Lombok into a class that currently has no Lombok annotations.
- For new classes, include **package** declarations that match existing packages in this project.

---

## MemoBom domain specifics

- Core tables:
  - `MB_TOPIC`  
    - Important columns: `TOPIC_NO` (PK), `OWNER_NO`, `UID` (UUID), `NAME`, etc.
  - `MB_TOPIC_FOLLOW`  
    - Important columns: `TOPIC_NO`, `USER_NO`, permission bits, etc.

Guidelines for Copilot:

- When writing queries related to directory trees:
  - Prefer **recursive SQL (`WITH RECURSIVE` or hierarchical queries)** instead of loading everything into memory.
  - Be careful not to produce N+1 style loops that hit the DB per row.
- When returning directory data to the UI:
  - Use **DTOs** that match the existing naming conventions, for example `DirectoryTreeNodeDto`, rather than exposing raw entities.
  - Include only necessary fields (`no`, `parentNo`, `uid`, `name`, permissions, hasChildren flag).

---

## Performance and resource usage

- Target environment is a **low-spec Raspberry Pi**.
- Avoid:
  - Unnecessary object allocations and heavy streams for hot paths.
  - Large in-memory trees; paginate or limit depth when possible.
  - Heavy libraries for trivial tasks.
- Prefer:
  - Simple loops over complex stream pipelines when performance matters.
  - Efficient, well-indexed SQL over in-memory filtering.

---

## Coding style

- Follow standard **Spring Boot** conventions.
- Use **English** for class, method, and variable names.  
  Use short inline comments when domain concepts need clarification.
- Error handling:
  - For expected business errors, throw **custom exceptions** (e.g. `DirectoryNotFoundException`, `AccessDeniedException`) and handle them in a **global exception handler**.
  - Log errors with enough context but **do not log sensitive data** (file paths that reveal user names, tokens, etc.).
- Logging:
  - Use `slf4j` (`log.info`, `log.warn`, `log.error`).
  - Do not generate excessive debug logs in performance-sensitive code.

---

## Thymeleaf + Tailwind UI

When generating HTML/Thymeleaf:

- Use **Thymeleaf attributes** (`th:each`, `th:text`, `th:if`, `th:href`) instead of raw string concatenation.
- Prefer **Tailwind-like utility classes** for layout and spacing (e.g. `flex`, `items-center`, `space-y-1`, `border`, `rounded`, `p-2`).
- For the **directory tree**:
  - Assume it is **folder-only** (no files in the tree).
  - Structure list items as nested `<ul><li>` blocks suitable for a collapsible tree view.

---

## Testing

- When proposing new logic, also suggest **JUnit tests**:
  - Service-level tests that cover directory creation, moving, deletion, and permission checks.
  - SQL-related tests using in-memory **H2** with schema close to the real DB.
- Keep tests deterministic and avoid external network calls.

---

## How to answer

- Prefer **short, direct answers with code examples** tailored to this project.
- When there are multiple options, **explain briefly which one fits MemoBom best** (e.g. memory-friendly, simple to maintain).
- Reuse existing patterns from this project when possible instead of introducing new ones.
