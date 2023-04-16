package com.La_nota.ALLA.Models;

import java.util.ArrayList;

public class TaskModel {
    private int position;

    private int status;
    private String task;

    private ArrayList<SubTaskModel> children;


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

    public void setChildrenList(ArrayList <SubTaskModel> children){
        this.children = children;
    }

    public void addNewChild(SubTaskModel child){
        children.add(child);
    }

}
