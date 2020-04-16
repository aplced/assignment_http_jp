package com.sumup.jobprocessor.exceptions;

public class TaskDependencyCircularException extends RuntimeException {
    public TaskDependencyCircularException(String task, Iterable<String> dependency) {
        super("Task " + task + " contains circular dependency [" + String.join(",", dependency) + "]");
    }
}