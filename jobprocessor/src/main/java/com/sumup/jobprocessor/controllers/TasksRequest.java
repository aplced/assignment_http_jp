package com.sumup.jobprocessor.controllers;

import com.sumup.jobprocessor.models.Task;
import lombok.Data;

import java.util.ArrayList;

@Data
public class TasksRequest {
    ArrayList<Task> tasks;
}
