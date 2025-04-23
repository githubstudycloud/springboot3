I'll search for information about modern software development best practices that are popular in the industry.

Let me search for additional information about modern software development best practices:

Let me search for one more specific area of best practices:

Based on my research, I'll now create a comprehensive project prompt template that incorporates modern software development best practices for 2025. This will help Claude understand your coding style, project organization, and collaboration expectations.

# Comprehensive Project Prompt for Claude Development Collaboration

## Project Overview
```
Project Name: [Your Project Name]
Description: [Brief description of project purpose and goals]
Repository URL: [If applicable]
Lead Developer: [Your name]
Team Members: [Other team members]
```

## Project Structure
```
[Project Root]/
├── src/                        # Source code
│   ├── main/                   # Main application code
│   └── test/                   # Test code
├── docs/                       # Documentation
│   ├── architecture/           # Architecture documents
│   ├── api/                    # API documentation
│   └── changes/                # Change records (detailed below)
├── config/                     # Configuration files
└── [other project-specific directories]
```

## Development Principles & Best Practices

### 1. Architecture Approach
- **Microservices Architecture**: Our application follows a microservices architecture, with each service performing a specific function and communicating over a network, providing flexibility, scalability, and rapid deployment.
- **API Gateway Pattern**: We implement API gateways as single entry points for all clients, allowing us to maintain a unified API and simplify client-side code.
- **Domain-Driven Design**: We design microservices around business capabilities using Domain-Driven Design (DDD), enabling high-level functionality and providing loosely coupled services.

### 2. DevOps Practices
- **CI/CD Pipeline**: Our development relies on CI/CD to integrate code changes frequently and automate the deployment process, reducing integration issues, detecting bugs early, and releasing updates faster.
- **Infrastructure as Code**: All infrastructure configuration is defined as code using tools like Terraform or Ansible.
- **Continuous Monitoring**: We implement comprehensive observability with tools for monitoring, logging, and tracing to maintain high standards of system quality and reliability.

### 3. Code Quality Standards
- **Clean Code Principles**: We prioritize readable, maintainable code that follows SOLID principles.
- **Test-Driven Development**: Critical components should be developed using TDD where practical.
- **Code Reviews**: All significant changes require peer review before merging.

### 4. Coding Style & Conventions

#### Naming Conventions
- **Classes**: PascalCase (e.g., `UserRepository`)
- **Methods/Functions**: camelCase (e.g., `getUserById`)
- **Variables**: camelCase (e.g., `userData`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
- **Private members**: Use leading underscore (e.g., `_privateVariable`)
- **Interfaces**: PascalCase with "I" prefix (e.g., `IUserService`)

#### Formatting
- **Indentation**: 4 spaces (no tabs)
- **Line length**: Maximum 100 characters
- **Braces**: Open brace on same line
  ```java
  if (condition) {
      // Code
  } else {
      // Code
  }
  ```
- **Imports**: Ordered by: standard library, third-party, project-specific

#### Documentation & Comments
- **Class comments**: Document purpose, responsibilities, and usage
- **Method comments**: Document functionality, parameters, return values, and exceptions
- **Inline comments**: Only for complex logic that isn't self-explanatory
- **TODO comments**: Format as `// TODO: [Description] ([Your initials], [Date])`

### 5. Resilience Patterns
- **Circuit Breaker Pattern**: We implement circuit breakers to monitor service interactions and prevent cascading failures when services are slow or failing.
- **Retry with Backoff**: Failed operations should implement exponential backoff strategy.
- **Fallback Mechanisms**: Critical services should provide fallback responses when primary functionality fails.

### 6. Security Practices
- **Secure by Design**: Security considerations are integrated from the start of development.
- **Least Privilege**: Services operate with minimal required permissions.
- **API Security**: All APIs use proper authentication and authorization.
- **Data Protection**: Sensitive data is encrypted both in transit and at rest.

## Change Management Process

### Change Record Format
For all significant changes, create a markdown file in `docs/changes/` with the following naming convention:
`YYYY-MM-DD-brief-description.md`

The content should follow this template:
```markdown
# [Change Title]

## Change Information
- **Type**: [Feature | Bug Fix | Refactoring | Documentation]
- **Date**: YYYY-MM-DD
- **Author**: [Your Name]
- **Priority**: [Low | Medium | High | Critical]
- **Affected Components**: [List of affected services/modules]

## Description
[Detailed description of the change, including the problem being solved]

## Implementation Details
[Technical details about how the change was implemented]

## Testing Strategy
[Description of how the change was tested]

## Rollback Plan
[Steps to rollback this change if needed]

## Related Issues/Tickets
[Links to related issues or tickets]
```

### Change Workflow
1. **Branch Creation**: Create a feature/bugfix branch from the main branch
2. **Development**: Implement the change with appropriate tests
3. **Documentation**: Update relevant documentation and create change record
4. **Code Review**: Submit for peer review
5. **Testing**: Ensure all tests pass, including integration tests
6. **Merge**: Merge to main branch after approval
7. **Deployment**: Deploy via CI/CD pipeline

## Collaboration Guidelines for Claude

### When Asking Claude for Help
1. **Provide Context**:
    - What feature/fix are you working on?
    - What files are involved?
    - What have you tried already?

2. **Be Specific**: Clearly state what you need help with:
    - Code generation
    - Code review
    - Debugging
    - Design ideas
    - Documentation

3. **Request Format Example**:
   ```
   I'm working on [feature/bug] in [component/service].
   
   Current file: src/main/java/com/example/service/UserService.java
   
   I need help with [specific request].
   
   Here's my current code:
   ```java
   // Paste relevant code here
   ```

   The expected behavior is [description].
   ```

### Claude's Response Guidelines
1. **Code Style**: Follow the project's coding conventions as defined above
2. **Best Practices**: Apply architecture/design patterns consistent with project approach
3. **Explanation**: Provide rationale for non-trivial solutions
4. **Alternative Approaches**: When relevant, mention alternative implementation options
5. **Testing Considerations**: Suggest test cases or testing approaches where applicable

## Project-Specific Information

### Key Technologies
- **Programming Languages**: [List main languages]
- **Frameworks**: [List main frameworks]
- **Databases**: [List databases]
- **Infrastructure**: [List infrastructure technologies]

### Environment Setup
- **Local Development**: [Instructions for local setup]
- **Testing Environment**: [How to access/use test environment]
- **Deployment Process**: [Overview of deployment process]

### Core Services/Components
- **[Service 1]**: [Brief description of responsibility]
- **[Service 2]**: [Brief description of responsibility]
- etc.

## Frequently Asked Questions
- **[Common Question 1]**: [Answer]
- **[Common Question 2]**: [Answer]
- etc.

---

This prompt template draws from modern software development best practices for 2025, focusing on:

1. **Microservices Architecture**: This architecture style has become the backbone of modern software development, offering scalability and flexibility.

2. **DevOps Integration**: DevOps practices enhance agility and reliability by automating the software delivery pipeline and ensuring collaborative development.

3. **Cloud-Native Development**: Cloud computing remains a cornerstone of software development, providing essential infrastructure and tools for modern app development.

4. **CI/CD Automation**: CI/CD pipelines are crucial for implementing fast, reliable software delivery processes with proper security measures.

5. **Design Patterns for Resilience**: Designing microservices to handle failure gracefully is crucial in 2025, using techniques like circuit breakers, retries, and fallbacks.

The template also includes detailed instructions for managing small changes and documenting project history, which will help both you and Claude maintain a comprehensive understanding of the project's evolution. You can customize this template to fit your specific project's needs and technology stack.