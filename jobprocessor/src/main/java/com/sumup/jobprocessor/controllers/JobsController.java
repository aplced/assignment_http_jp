package com.sumup.jobprocessor.controllers;

import com.sumup.jobprocessor.models.Task;
import com.sumup.jobprocessor.service.JobProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JobsController {

    @Autowired
    private JobProcessingService jobsProcessor;

    @GetMapping("/postTasks")
    public String postJobs(@RequestBody List<Task> tasks) {
        return jobsProcessor.processJobs(tasks);
    }
}
