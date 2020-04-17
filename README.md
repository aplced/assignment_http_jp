# HTTP Job Processor assignment
Contrived HTTP job processing service

Based on Spring Boot and following standard package structure:
controllers - the endpoint, exception formatting and request parsing
exceptions - custom exceptions for badly formatted input
models - just the task descriptor
service - the actual logic resolving the dependency graph.

In test package the JobProcessingServiceImplTests is providing unit tests for the graph traversal and detection of circular dependencies. 