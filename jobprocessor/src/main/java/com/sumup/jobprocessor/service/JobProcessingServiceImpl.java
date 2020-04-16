package com.sumup.jobprocessor.service;

import com.sumup.jobprocessor.exceptions.TaskDependencyCircularException;
import com.sumup.jobprocessor.exceptions.TaskDependencyUnsatisfiedException;
import com.sumup.jobprocessor.models.Task;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class JobProcessingServiceImpl implements JobProcessingService {
    @Override
    public String processJobs(List<Task> tasks) {
        Map<String, Task> tasksByName = tasks.stream().collect(Collectors.toMap(Task::getName, Function.identity()));

        Map<String, Set<String>> dependenciesByName = allDependenciesPresent(tasksByName);
        Set<Task> orderedJobs = buildTaskOrder(dependenciesByName, tasksByName);

        List<String> processedOutput = orderedJobs.stream().map(task -> task.getCommand()).collect(Collectors.toList());
        Collections.reverse(processedOutput);

        return String.join("\n", processedOutput);
    }

    private Map<String, Set<String>> allDependenciesPresent(Map<String, Task> tasksByName) {
        Map<String, Set<String>> dependenciesByName = new HashMap<>();

        tasksByName.values().stream().forEach(task -> {
            LinkedHashSet<String> orderedRequiredTasks = new LinkedHashSet <>();
            buildTaskRequiredOrder(task, tasksByName, orderedRequiredTasks);
            if(orderedRequiredTasks.size() > 0) {
                dependenciesByName.put(task.getName(), orderedRequiredTasks);
            }
        });

        return dependenciesByName;
    }

    private void buildTaskRequiredOrder(Task task, Map<String, Task> tasksByName, LinkedHashSet<String> orderedRequiredTasks) {

        Optional.ofNullable(task.getRequires())
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .forEach(requiredTaskName -> {
            if(!tasksByName.containsKey(requiredTaskName)){
                throw new TaskDependencyUnsatisfiedException(task.getName(), requiredTaskName);
            } else {
                Task requiredTask = tasksByName.get(requiredTaskName);
                if(orderedRequiredTasks.add(requiredTask.getName())) {
                    buildTaskRequiredOrder(requiredTask, tasksByName, orderedRequiredTasks);
                } else {
                    boolean pastDuplicateDependency = false;
                    for(String taskDependency : orderedRequiredTasks){
                        if(taskDependency.equals(requiredTask)){
                            pastDuplicateDependency = true;
                        } else if(pastDuplicateDependency) {
                            if(tasksByName.get(taskDependency).getRequires().contains(requiredTask.getName())){
                                throw new TaskDependencyCircularException(task.getName(), orderedRequiredTasks);
                            }
                        }
                    }
                }
            }
        });
    }

    private Set<Task> buildTaskOrder(Map<String, Set<String>> dependenciesByName, Map<String, Task> tasksByName) {
        Set<Task> orderedTasks = new LinkedHashSet<>();

        dependenciesByName
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry -> ((Map.Entry<String, Set<String>>)entry).getValue().size()).reversed())
                .forEachOrdered(entry -> {
            if(tasksByName.containsKey(entry.getKey())) {
                orderedTasks.add(tasksByName.get(entry.getKey()));
                entry.getValue().stream().forEachOrdered(dependency -> {
                    if(tasksByName.containsKey(dependency)) {
                        orderedTasks.add(tasksByName.get(dependency));
                    }
                });
            }
        });

        return orderedTasks;
    }
}
