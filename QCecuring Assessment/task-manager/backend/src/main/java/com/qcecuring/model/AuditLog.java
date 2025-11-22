package com.qcecuring.model;

public class AuditLog {

    private Integer id;
    private String timestamp;
    private String action;
    private Integer taskId;
    private String updatedContent;

    // ----------- Constructors -----------
    public AuditLog() {}

    public AuditLog(Integer id, String timestamp, String action, Integer taskId, String updatedContent) {
        this.id = id;
        this.timestamp = timestamp;
        this.action = action;
        this.taskId = taskId;
        this.updatedContent = updatedContent;
    }

    // ----------- Getters & Setters -----------
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getUpdatedContent() {
        return updatedContent;
    }

    public void setUpdatedContent(String updatedContent) {
        this.updatedContent = updatedContent;
    }
}
