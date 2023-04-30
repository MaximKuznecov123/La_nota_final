package com.La_nota.ALLA.Adapters;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.La_nota.ALLA.Activities.TaskUpdateCreate;
import com.La_nota.ALLA.Models.TaskModel;
import com.La_nota.ALLA.R;
import com.La_nota.ALLA.Utils.TasksHandler2;

import java.util.List;
import java.util.Random;

public class TaskAdapter2 extends RecyclerView.Adapter<TaskAdapter2.ViewHolder> {

    private List<TaskModel> todolist;
    private final TasksHandler2 db;

    public TaskAdapter2(TasksHandler2 db) {
        this.db = db;
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
        TaskModel item = todolist.get(position);

        holder.name.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.checkBox.setChecked(item.getStatus());

        if (item.getStatus()) {
            holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.description.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.description.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else {
                holder.name.setPaintFlags(holder.name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                holder.description.setPaintFlags(holder.name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }

            db.updateStatus(item.getID(), isChecked?1:0, item.getFrequency() == 0);
        });

        holder.view.setOnClickListener((v) -> {
            editItem(position);
        });
    }


    @Override
    public int getItemCount() {
        return todolist.size();
    }


    public void setTasks(List<TaskModel> todolist){
        this.todolist = todolist;
        notifyDataSetChanged();
    }

    public void editItem(int position) {
        TaskModel item = todolist.get(position);

        Bundle data = new Bundle();
        data.putString(TaskUpdateCreate.TASK_TITLE_EXTRA, item.getTitle());
        data.putString(TaskUpdateCreate.TASK_DESCR_EXTRA, item.getDescription());
        data.putInt(TaskUpdateCreate.ID_EXTRA, item.getID());
        data.putInt(TaskUpdateCreate.FREQUENCY_EXTRA, item.getFrequency());

        Intent i = new Intent(VPadapter.getActivity(), TaskUpdateCreate.class);
        i.putExtra(TaskUpdateCreate.BUNDLE_EXTRA, data);

        VPadapter.getActivity().startActivity(i);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        CheckBox checkBox;
        TextView name, description;
        ImageView bar;

        ViewHolder(View view) {
            super(view);
            this.view = view;

            checkBox = view.findViewById(R.id.todocheckBox);
            name = view.findViewById(R.id.name);
            description = view.findViewById(R.id.descr);

            bar = view.findViewById(R.id.color_bar);
            int a = new Random().nextInt(2);
            bar.getBackground().setTint(VPadapter.getActivity().getResources().getColor(a == 1 ? R.color.purple_200 : R.color.purple_700, null));
            view.getBackground().setTint(VPadapter.getActivity().getResources().getColor(a == 1 ? R.color.purple_200 : R.color.purple_700, null));
        }
    }
}