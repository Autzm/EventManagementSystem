package com.eventapp.model;

public class Task {
    public int id;
    public String title;
    public String assignedTo;
    public String status;

    public Task(int id, String title, String assignedTo, String status) {
        this.id = id;
        this.title = title;
        this.assignedTo = assignedTo;
        this.status = status;
    }

    public Task(String title, String assignedTo, String status) {
        this.title = title;
        this.assignedTo = assignedTo;
        this.status = status;
    }
}