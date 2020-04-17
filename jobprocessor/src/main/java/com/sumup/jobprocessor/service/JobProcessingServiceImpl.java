package com.sumup.jobprocessor.service;

import com.sumup.jobprocessor.exceptions.TaskDependencyCircularException;
import com.sumup.jobprocessor.exceptions.TaskDependencyUnsatisfiedException;
import com.sumup.jobprocessor.models.Task;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JobProcessingServiceImpl implements JobProcessingService {
    @Override
    public String processJobs(List<Task> tasks) {
        Map<String, Task> taskGraph = tasks.stream().collect(Collectors.toMap(Task::getName, Function.identity()));
        Set<String> resolved = new LinkedHashSet<>();
        Set<String> visited = new HashSet<>();

        taskGraph.entrySet().forEach(taskNode -> {
            resolveRequired(taskNode.getKey(), taskGraph, resolved, visited);
        });

        StringBuilder cmdOutput = new StringBuilder();
        for(String task : resolved){
            cmdOutput.append(taskGraph.get(task).getCommand() + "\n");
        }

        return cmdOutput.toString();
    }

    private void resolveRequired(String taskName, Map<String, Task> taskGraph, Set<String> resolved, Set<String> visited) {
        visited.add(taskName);
        if(taskGraph.containsKey(taskName)) {
            taskGraph.get(taskName).getRequires().forEach(requiredTask -> {
                if (!resolved.contains(requiredTask)) {
                    if (visited.contains(requiredTask)) {
                        throw new TaskDependencyCircularException(taskName, taskGraph.get(taskName).getRequires());
                    } else {
                        resolveRequired(requiredTask, taskGraph, resolved, visited);
                    }
                }
            });
        } else {
            throw new TaskDependencyUnsatisfiedException("", taskName);
        }
        resolved.add(taskName);
    }

}
