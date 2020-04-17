package com.sumup.jobprocessor.models;

import lombok.Data;

import java.util.List;

@Data
public class Task {
    private String name;
    private String command;
    private List<String> requires;
}
