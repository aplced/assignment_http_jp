package com.sumup.jobprocessor;

import com.sumup.jobprocessor.exceptions.TaskDependencyCircularException;
import com.sumup.jobprocessor.exceptions.TaskDependencyUnsatisfiedException;
import com.sumup.jobprocessor.models.Task;
import com.sumup.jobprocessor.service.JobProcessingService;
import com.sumup.jobprocessor.service.JobProcessingServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class JobProcessingServiceImplTests {
    @TestConfiguration
    static class JobProcessingServiceImplTestsContextConfiguration {

        @Bean
        public JobProcessingService jobProcessingService() {
            return new JobProcessingServiceImpl();
        }
    }

    @Autowired
    private JobProcessingService jobProcessingService;

    @Test(expected = TaskDependencyUnsatisfiedException.class)
    public void unsatisfiedDependencyTaskThrowsException() {
        List<Task> jobs = new ArrayList<>();

        Task task1 = new Task();
        task1.setName("task-1");
        task1.setCommand("touch /tmp/file1");
        jobs.add(task1);

        Task task2 = new Task();
        task2.setName("task-2");
        task2.setCommand("cat /tmp/file1");
        task2.setRequires(Arrays.asList("task-3"));
        jobs.add(task2);

        jobProcessingService.processJobs(jobs);
    }

    @Test(expected = TaskDependencyCircularException.class)
    public void circularDependencyTaskListThrowsException() {
        List<Task> jobs = new ArrayList<>();

        Task task1 = new Task();
        task1.setName("task-1");
        task1.setCommand("touch /tmp/file1");
        task1.setRequires(Arrays.asList("task-2"));
        jobs.add(task1);

        Task task2 = new Task();
        task2.setName("task-2");
        task2.setCommand("cat /tmp/file1");
        task2.setRequires(Arrays.asList("task-3"));
        jobs.add(task2);

        Task task3 = new Task();
        task3.setName("task-3");
        task3.setCommand("echo 'Hello World!' > /tmp/file1");
        task3.setRequires(Arrays.asList("task-1"));
        jobs.add(task3);

        jobProcessingService.processJobs(jobs);
    }

    @Test
    public void exampleTaskListProcessedCorrectly() {

        String expectedOutput = "touch /tmp/file1\n" +
                                "echo 'Hello World!' > /tmp/file1\n" +
                                "cat /tmp/file1\n" +
                                "rm /tmp/file1";

        List<Task> jobs = new ArrayList<>();

        Task task1 = new Task();
        task1.setName("task-1");
        task1.setCommand("touch /tmp/file1");
        jobs.add(task1);

        Task task2 = new Task();
        task2.setName("task-2");
        task2.setCommand("cat /tmp/file1");
        task2.setRequires(Arrays.asList("task-3"));
        jobs.add(task2);

        Task task3 = new Task();
        task3.setName("task-3");
        task3.setCommand("echo 'Hello World!' > /tmp/file1");
        task3.setRequires(Arrays.asList("task-1"));
        jobs.add(task3);

        Task task4 = new Task();
        task4.setName("task-4");
        task4.setCommand("rm /tmp/file1");
        task4.setRequires(Arrays.asList("task-2", "task-3"));
        jobs.add(task4);

        String result = jobProcessingService.processJobs(jobs);

        assertEquals(expectedOutput, result);
    }
}
