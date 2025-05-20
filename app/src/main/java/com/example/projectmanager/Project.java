package com.example.projectmanager;

public class Project {
    private int id;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private int userId;

    public Project(int id, String name, String description, String startDate, String endDate, int userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userId = userId;
    }

    // Getters y setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public int getUserId() { return userId; }
}