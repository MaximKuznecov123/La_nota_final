package com.example.myapplication.Adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.Activities.MainActivity;
import com.example.myapplication.Activities.TaskUpdateCreate;
import com.example.myapplication.R;
import com.example.myapplication.Models.TaskModel2;
import com.example.myapplication.Utils.TasksHandler2;

import java.util.List;
import java.util.Random;

public class TaskAdapter2 extends RecyclerView.Adapter<TaskAdapter2.ViewHolder> {

    private List<TaskModel2> todolist;
    private static MainActivity activity;
    private final TasksHandler2 db;
    private final String curdate;

    public TaskAdapter2(TasksHandler2 db, MainActivity activity, String curdate){
        this.db = db;
        this.activity = activity;
        this.curdate = curdate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        db.openDB();
        TaskModel2 item = todolist.get(position);
        holder.Name.setText(item.getTask());
        holder.Description.setText(item.getDescription());
        holder.task.setChecked(tobool(item.getStatus()));

        holder.task.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int transpos = todolist.size() - position;
            db.updateStatus(curdate, transpos ,isChecked?1:0);
        });

        holder.view.setOnClickListener((v) -> {
            editItem(position);
        });
    }
    public boolean tobool(int  n){
        return n!=0;
    }

    @Override
    public int getItemCount() {
        return todolist.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTasks(List<TaskModel2> todolist){
        this.todolist = todolist;
        notifyDataSetChanged();
    }

    public void editItem(int position){
        TaskModel2 item = todolist.get(position);
            Bundle bundle = new Bundle();

            bundle.putString(TaskUpdateCreate.BundleTaskName, item.getTask());
            bundle.putString(TaskUpdateCreate.BundleTaskDescr, item.getDescription());

            Intent i = new Intent(activity, TaskUpdateCreate.class);
            i.putExtra(TaskUpdateCreate.BundleExtra, bundle);
            i.putExtra(TaskUpdateCreate.DateExtra, curdate);
            i.putExtra(TaskUpdateCreate.PositionExtra, item.getPosition());

            activity.startActivity(i);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        CheckBox task;
        TextView Name, Description;
        ImageView bar;

        ViewHolder(View view) {
            super(view);
            this.view = view;

            task = view.findViewById(R.id.todocheckBox);
            Name = view.findViewById(R.id.name);
            Description = view.findViewById(R.id.description);

            bar = view.findViewById(R.id.color_bar);
            int a = new Random().nextInt(2);
            Drawable drawable = AppCompatResources.getDrawable(activity,R.drawable.event_list_colorbar);
            drawable.setTint(activity.getResources().getColor(a == 1 ? R.color.purple_200 : R.color.purple_700));
            bar.setBackground(drawable);
        }
    }
}