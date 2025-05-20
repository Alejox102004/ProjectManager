package com.example.projectmanager;

public class Activity {
    private int id;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String status;
    private int projectId;

    public Activity(int id, String name, String description, String startDate, String endDate, String status, int projectId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.projectId = projectId;
    }

    // Getters y setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public int getProjectId() { return projectId; }
}