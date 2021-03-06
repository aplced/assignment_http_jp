package com.sumup.jobprocessor;

import com.sumup.jobprocessor.exceptions.TaskDependencyCircularException;
import com.sumup.jobprocessor.exceptions.TaskDependencyUnsatisfiedException;
import com.sumup.jobprocessor.models.Task;
import com.sumup.jobprocessor.service.JobProcessingService;
import com.sumup.jobprocessor.service.JobProcessingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
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

    @Test
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

        Assertions.assertThrows(TaskDependencyUnsatisfiedException.class, () -> jobProcessingService.processJobs(jobs));
    }

    @Test
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

        Assertions.assertThrows(TaskDependencyCircularException.class, () -> jobProcessingService.processJobs(jobs));
    }

        @Test
        public void convolutedDependency() {
        String expectedOutput = "A\nB\nD\nC\nE\nF\n";
        List<Task> jobs = new ArrayList<>();

        Task A = new Task();
        A.setName("A");
        A.setCommand("A");
        jobs.add(A);

        Task B = new Task();
        B.setName("B");
        B.setCommand("B");
        B.setRequires(Arrays.asList("A"));
        jobs.add(B);

        Task C = new Task();
        C.setName("C");
        C.setCommand("C");
        C.setRequires(Arrays.asList("D", "A"));
        jobs.add(C);

        Task D = new Task();
        D.setName("D");
        D.setCommand("D");
        D.setRequires(Arrays.asList("B"));
        jobs.add(D);

        Task E = new Task();
        E.setName("E");
        E.setCommand("E");
        E.setRequires(Arrays.asList("C", "A"));
        jobs.add(E);

        Task F = new Task();
        F.setName("F");
        F.setCommand("F");
        F.setRequires(Arrays.asList("E", "B"));
        jobs.add(F);

        String result = jobProcessingService.processJobs(jobs);

        Assertions.assertEquals(expectedOutput, result);
    }

    @Test
    public void exampleTaskListProcessedCorrectly() {

        String expectedOutput = "touch /tmp/file1\n" +
                                "echo 'Hello World!' > /tmp/file1\n" +
                                "cat /tmp/file1\n" +
                                "rm /tmp/file1\n";

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

        Assertions.assertEquals(expectedOutput, result);
    }
}
