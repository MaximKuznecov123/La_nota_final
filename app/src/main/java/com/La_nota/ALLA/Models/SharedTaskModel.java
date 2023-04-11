package com.La_nota.ALLA.Models;

public class SharedTaskModel {

    private String task, description;
    private int frequency;

    public void setAll(int frequency,
                      String task, String description){
        this.frequency = frequency;
        this.task = task;
        this.description = description;
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

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
