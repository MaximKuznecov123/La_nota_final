package com.La_nota.ALLA.Models;

public class BasicTaskModel {
    private int position;
    private int id;

    private int status;
    private String task, description;
    private boolean shared = false;


    public void setAll(int position,
                       int status, String task, String description){
        this.position = position;
        this.status = status;
        this.task = task;
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPosition() {
        return position;
    }

    public boolean isShared() {
        return shared;
    }

    public void setSharedtrue(boolean shared) {
        this.shared = shared;
    }

    public int getId() {
        return id;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setId(int id) {
        this.id = id;
    }
}
