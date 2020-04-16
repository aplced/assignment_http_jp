package com.sumup.jobprocessor.controllers;

import com.sumup.jobprocessor.exceptions.TaskDependencyCircularException;
import com.sumup.jobprocessor.exceptions.TaskDependencyUnsatisfiedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class TaskDependencyExceptionAdvice {
    @ResponseBody
    @ExceptionHandler(TaskDependencyUnsatisfiedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String taskDependencyUnsatisfiedHandler(TaskDependencyUnsatisfiedException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(TaskDependencyCircularException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String taskDependencyCircularHandler(TaskDependencyCircularException ex) {
        return ex.getMessage();
    }
}