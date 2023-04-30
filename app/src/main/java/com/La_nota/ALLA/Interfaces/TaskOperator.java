package com.La_nota.ALLA.Interfaces;

import com.La_nota.ALLA.Models.TaskModel;

import java.util.List;

public interface TaskOperator {

    List<TaskModel> getTasks(String date);

    void insertTask(TaskModel task, String date);
    void updateStatus(int id, int status);
    void updateTitle(int id, String title);
    void updateDescr(int id, String descr);


    void deleteTask(int id);
}
