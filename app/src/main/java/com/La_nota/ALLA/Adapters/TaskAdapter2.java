package com.La_nota.ALLA.Adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.La_nota.ALLA.Activities.TaskUpdateCreate;
import com.La_nota.ALLA.Models.BasicTaskModel;
import com.La_nota.ALLA.R;
import com.La_nota.ALLA.Utils.TasksHandler2;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskAdapter2 extends RecyclerView.Adapter<TaskAdapter2.ViewHolder> {

    private List<BasicTaskModel> todolist;
    private final TasksHandler2 db;
    private final String curdate;

    public TaskAdapter2(TasksHandler2 db, String curdate) {
        this.db = db;
        this.curdate = curdate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        db.openDB();
        BasicTaskModel item = todolist.get(position);
        holder.Name.setText(item.getTask());
        holder.Description.setText(item.getDescription());
        holder.task.setChecked(tobool(item.getStatus()));

        holder.task.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (item.isShared()) {
                db.updateSHStatus(curdate, item.getId(), isChecked ? 1 : 0);
            } else {
                int transpos = todolist.size() - position;
                db.updateStatus(curdate, transpos, isChecked ? 1 : 0);
            }
        });

        holder.view.setOnClickListener((v) -> {
            editItem(position);
        });
    }

    public boolean tobool(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return todolist.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTasks(List<BasicTaskModel> basiclist) {
        this.todolist = basiclist;
        notifyDataSetChanged();
    }

    public void editItem(int position) {
        //TODO сделать тоже самое для долённых заданий
        BasicTaskModel item = todolist.get(position);
        Bundle bundle = new Bundle();

        bundle.putString(TaskUpdateCreate.BUNDLE_TASK_NAME, item.getTask());
        bundle.putString(TaskUpdateCreate.BUNDLE_TASK_DESCR, item.getDescription());

        ArrayList<Integer> indexesOfShared = new ArrayList<>();

        for (BasicTaskModel model :
                todolist) {
            if (model.isShared()) {
                indexesOfShared.add(model.getPosition());
            }
        }

        Log.d("MYLOGindexes", indexesOfShared.toString());

        Intent i = new Intent(VPadapter.getActivity(), TaskUpdateCreate.class);
        i.putExtra(TaskUpdateCreate.BUNDLE_EXTRA, bundle);
        i.putExtra(TaskUpdateCreate.DATE_EXTRA, curdate);
        i.putExtra(TaskUpdateCreate.POSITION_EXTRA, item.getPosition());
        i.putExtra(TaskUpdateCreate.INDEXES_OF_SHARED_EXTRA, indexesOfShared);
        i.putExtra(TaskUpdateCreate.IS_SH, item.isShared());


        VPadapter.getActivity().startActivity(i);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
            Drawable drawable = AppCompatResources.getDrawable(VPadapter.getActivity(), R.drawable.event_list_colorbar);
            drawable.setTint(VPadapter.getActivity().getResources().getColor(a == 1 ? R.color.purple_200 : R.color.purple_700));
            bar.setBackground(drawable);
        }

    }
}