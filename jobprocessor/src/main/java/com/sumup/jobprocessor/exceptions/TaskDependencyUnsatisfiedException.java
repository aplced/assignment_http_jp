package com.sumup.jobprocessor.exceptions;

public class TaskDependencyUnsatisfiedException extends RuntimeException {
    public TaskDependencyUnsatisfiedException(String task, String dependency) {
        super("Task " + task + " required dependency [" + dependency + "] not found");
    }
}