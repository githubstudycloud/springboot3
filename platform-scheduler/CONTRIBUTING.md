# Contributing to Platform Scheduler

Thank you for your interest in contributing to the Platform Scheduler module! This document provides guidelines and instructions for contributing to this project.

## Code of Conduct

By participating in this project, you agree to uphold our Code of Conduct, which ensures a respectful and inclusive environment for all contributors.

## How to Contribute

There are many ways to contribute to the Platform Scheduler:

- Reporting bugs and issues
- Suggesting new features or enhancements
- Improving documentation
- Submitting code changes and fixes
- Reviewing pull requests

### Reporting Issues

When reporting issues, please use the issue template and include:

1. A clear and descriptive title
2. Steps to reproduce the issue
3. Expected behavior
4. Actual behavior
5. Environment details (OS, Java version, etc.)
6. Logs or screenshots if applicable

### Feature Requests

For feature requests, please:

1. Check if the feature has already been requested
2. Provide a clear description of the feature
3. Explain the use case and benefits
4. Suggest an implementation approach if possible

### Submitting Code Changes

1. **Fork the repository** to your GitHub account
2. **Create a feature branch** from the `develop` branch:
   ```bash
   git checkout -b feature/your-feature-name develop
   ```
3. **Implement your changes** following the coding standards
4. **Write or update tests** to cover your changes
5. **Ensure all tests pass** by running:
   ```bash
   mvn test
   ```
6. **Submit a pull request** to the `develop` branch

## Development Setup

### Prerequisites

- Java 21 or higher
- Maven 3.8+ or Gradle 7.5+
- MySQL 5.7+ or compatible database
- Redis 7.x+
- Git

### Local Development Environment

1. **Clone your fork**:
   ```bash
   git clone https://github.com/your-username/platform.git
   cd platform
   ```

2. **Set up the database** (see [SETUP.md](SETUP.md) for details)

3. **Build the project**:
   ```bash
   mvn clean install -pl platform-scheduler -am
   ```

4. **Run the application with development profile**:
   ```bash
   mvn spring-boot:run -pl platform-scheduler -Dspring.profiles.active=dev
   ```

### Code Style and Standards

This project follows specific coding standards:

1. **Code Formatting**:
   - Use 4 spaces for indentation (not tabs)
   - Line length should not exceed 120 characters
   - Follow Java naming conventions

2. **Domain-Driven Design Principles**:
   - Keep domain models framework-agnostic
   - Use ubiquitous language consistently
   - Follow the hexagonal architecture pattern
   - Separate domain logic from infrastructure concerns

3. **Documentation**:
   - All public APIs must have comprehensive Javadoc
   - Include examples for complex operations
   - Document domain concepts and design decisions

4. **Testing**:
   - Write unit tests for all domain logic
   - Write integration tests for repositories and services
   - Maintain at least 80% code coverage

### Commit Guidelines

Follow these guidelines for your commits:

1. **Use semantic commit messages**:
   - feat: Add new feature
   - fix: Fix a bug
   - docs: Update documentation
   - style: Format code (no functional changes)
   - refactor: Refactor code (no functional changes)
   - test: Add or update tests
   - chore: Update build tasks, package manager, etc.

2. **Keep commits focused**:
   - Each commit should represent a single logical change
   - Don't mix unrelated changes in a single commit

3. **Write meaningful commit messages**:
   - Use the imperative mood ("Add feature" not "Added feature")
   - First line should be 50 characters or less
   - Follow with a blank line and detailed explanation if needed

## Pull Request Process

1. **Target the develop branch** for all PRs
2. **Update documentation** to reflect any changes
3. **Add or update tests** to cover your changes
4. **Ensure CI passes** before requesting a review
5. **Request a review** from maintainers
6. **Address review feedback** promptly
7. **Squash commits** if requested by maintainers

## Architecture Guidelines

When contributing to this project, follow these architectural principles:

### Domain Layer

- Keep domain models pure and framework-independent
- Use Value Objects for immutable concepts
- Use Entities for objects with identity and state
- Implement Domain Events for important state changes
- Define Repository interfaces in the domain layer

### Application Layer

- Services should orchestrate domain objects to fulfill use cases
- Use DTOs for external communication
- Implement CQRS pattern for complex operations
- Keep transaction boundaries in the application layer

### Infrastructure Layer

- Implement Repository interfaces with persistence adapters
- Provide adapters for external services
- Handle technical concerns (caching, transactions, etc.)
- Keep infrastructure code isolated from domain logic

### Interface Layer

- REST controllers should be thin and delegate to application services
- Use proper HTTP methods and status codes
- Document API endpoints with OpenAPI/Swagger
- Implement comprehensive error handling

## Testing Guidelines

### Unit Testing

- Test domain logic in isolation
- Use mocks or stubs for dependencies
- Focus on behavior, not implementation details
- Follow the Arrange-Act-Assert pattern

### Integration Testing

- Test repository implementations against a real database
- Test REST controllers with MockMvc
- Test external service integrations with WireMock
- Use appropriate test profiles and containers

### Performance Testing

- Test scheduler performance with large job sets
- Test distributed execution in a cluster
- Measure and optimize resource usage

## Documentation

### Code Documentation

- Use meaningful variable and method names
- Add Javadoc for all public APIs
- Document parameters, return values, and exceptions
- Include examples for complex methods

### Technical Documentation

- Update README.md with new features
- Document architectural decisions in ADRs
- Maintain up-to-date setup and configuration guides
- Document external integrations

## Release Process

1. Merge feature branches into `develop`
2. Create a release branch from `develop` when ready
3. Perform final testing and documentation updates
4. Create a pull request to `main`
5. After approval, merge to `main` and tag with version
6. Update version numbers in `develop`

## License

By contributing to this project, you agree that your contributions will be licensed under the project's license.

Thank you for contributing to the Platform Scheduler module!
