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
- `DocumentChecker` interface with implementations for different file types (PDF, Word, Excel, PowerPoint, CSV, Images, ZIP)
- `DocumentSanitizer` interface for cleaning uploaded files
- Uses Aspose libraries for document inspection and iTextPDF for PDFs

**web-server-sql-injection**: Demonstrates SQL injection prevention:
- Secure endpoints use `PreparedStatement` or `JdbcTemplate` with parameterized queries
- Insecure endpoints use `Statement` with string concatenation (for demonstration)
- Uses Flyway for database migrations in `src/main/resources/db/migration/`
- Requires MySQL database running on port 3308 (use `docker-compose up`)

**web-server-cors**: Shows CORS configuration via `GlobalCORSConfiguration` implementing `WebMvcConfigurer`

**web-server-csrf**: Configures CSRF with `CookieCsrfTokenRepository.withHttpOnlyFalse()`

**web-server-firewall**: Customizes `StrictHttpFirewall` to restrict allowed HTTP methods

**web-server-security**: Configures security headers including CSP, HSTS, X-Frame-Options, X-Content-Type-Options, etc.

## Dependencies

The project uses Spring Boot 3.3.6 with Java 17. The parent POM manages versions for:
- MySQL Connector
- Flyway (database migrations)
- Aspose (Words, Cells, Slides for document processing)
- iTextPDF
- OpenCSV
- Jakarta Validation API

## Database

The SQL injection module requires MySQL. Start with:
```bash
docker-compose up
```

This starts MySQL on port 3308 with database `sql-injection`, user `davis`, password `test`.