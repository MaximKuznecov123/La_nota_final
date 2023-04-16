package com.La_nota.ALLA.Models;

public class SubTaskModel {

    private int rootPos;
    private int position;

    private int status;
    private String task;


    public void setAll(int position,
                       int status, String task){
        this.position = position;
        this.status = status;
        this.task = task;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getRootPos() {
        return rootPos;
    }

    public void setRootPos(int rootPos) {
        this.rootPos = rootPos;
    }
}
