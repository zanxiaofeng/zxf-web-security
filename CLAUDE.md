# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot multi-module project demonstrating web security vulnerabilities and their mitigations. Each module is a standalone application showcasing a specific security concept.

## Build and Run

Build all modules from the root:
```bash
mvn clean install
```

Build a specific module:
```bash
cd <module-name>
mvn clean install
```

Run a specific module (after building):
```bash
cd <module-name>
mvn spring-boot:run
```

Or run the JAR directly:
```bash
java -jar <module-name>/target/<module-name>-0.0.1-SNAPSHOT.jar
```

Run OpenRewrite automated migrations (Spring Boot 3.5, Java 21, security best practices):
```bash
mvn rewrite:run
```

## Testing

Only `web-server-fileupload` has automated tests. The test in `FileUploadControllerTest` is an **integration test** that requires the server to be running on port 8085 — run it manually after starting the module.

To test CORS behavior manually, use the provided script:
```bash
cd web-server-cors && bash test.sh
```

The `web-server-tester` module (port 8081) hosts static HTML pages for browser-based manual testing of all other modules.

## Module Structure

Each module is an independent Spring Boot application with its own port:

| Module | Port | Purpose |
|--------|------|---------|
| web-server-tester | 8081 | Testing utility, serves test pages at http://localhost:8081 |
| web-server-cors | 8082 | Cross-Origin Resource Sharing (CORS) configuration |
| web-server-csrf | 8083 | Cross-Site Request Forgery (CSRF) protection |
| web-server-security | 8084 | HTTP security headers (CSP, HSTS, X-Frame-Options, etc.) |
| web-server-fileupload | 8085 | File upload validation and sanitization |
| web-server-sql-injection | 8086 | SQL injection prevention with JdbcTemplate |
| web-server-firewall | 8087 | Spring Security HTTP firewall configuration |

## Architecture

### Common Patterns

- Each module has its own `MyApplication` class as the Spring Boot entry point
- Controllers are in `controllers/` package
- Security configuration is in `security/WebSecurityConfig.java`
- FreeMarker templates (`.ftlh`) are in `src/main/resources/templates/`
- Application properties are in `src/main/resources/application.properties`

### Key Modules Details

**web-server-fileupload**: Demonstrates secure file upload handling using a registry pattern:
- `DocumentType` enum maps file extensions to `DocumentChecker` and `DocumentSanitizer` implementations
- Supported types: PDF (iTextPDF), Word/Excel/PowerPoint (Aspose), CSV (OpenCSV), Image, ZIP
- `DocumentChecker.isSafe()` validates file content; `DocumentSanitizer.sanitize()` strips malicious content
- Only CSV has a sanitizer implementation (`CsvDocumentSanitizer`); other unsafe files are rejected outright
- `ExchangeController` demonstrates file exchange with an external service via `RestTemplate`

**web-server-sql-injection**: Demonstrates SQL injection prevention:
- Secure endpoints (`/security/**`) use `PreparedStatement` or `JdbcTemplate`/`NamedParameterJdbcTemplate` with parameterized queries
- Insecure endpoints (`/un-security/**`) use `Statement` with string concatenation (for demonstration only)
- Uses Flyway for database migrations in `src/main/resources/db/migration/`
- Requires MySQL database running on port 3308 (use `docker-compose up`)

**web-server-cors**: CORS is configured via `GlobalCORSConfiguration` (implements `WebMvcConfigurer`), which disables cross-origin access to `/my/**`. Also has a `WebSecurityConfig` that allows GET/POST/PUT via `StrictHttpFirewall`.

**web-server-csrf**: Configures CSRF with `CookieCsrfTokenRepository.withHttpOnlyFalse()` so JavaScript can read the XSRF-TOKEN cookie.

**web-server-firewall**: Restricts HTTP methods to GET only via `StrictHttpFirewall` in `WebSecurityConfig`.

**web-server-security**: Configures response security headers via `WebSecurityConfig`: Cache-Control, X-Frame-Options, X-Content-Type-Options, XSS-Protection, HSTS, Referrer-Policy, and CSP (`default-src 'self'`). Also has an `XMLController` for XXE demonstration.

## Dependencies

The project uses Spring Boot 3.3.6 with Java 17. The parent POM manages versions for:
- MySQL Connector
- Flyway (database migrations)
- Aspose (Words, Cells, Slides for document processing) — fetched from `https://releases.aspose.com/java/repo/`
- iTextPDF
- OpenCSV
- Jakarta Validation API

## Database

The SQL injection module requires MySQL. Start with:
```bash
docker-compose up
```

This starts MySQL on port 3308 with database `sql-injection`, user `davis`, password `test`.