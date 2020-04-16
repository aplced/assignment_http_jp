package com.sumup.jobprocessor.service;

import com.sumup.jobprocessor.models.Task;

import java.util.List;

public interface JobProcessingService {
    String processJobs(List<Task> tasks);
}
