package com.example.myapplication.Models;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activities.MainActivity;
import com.example.myapplication.Adapters.TaskAdapter2;
import com.example.myapplication.Adapters.VPadapter;
import com.example.myapplication.R;
import com.example.myapplication.Utils.TasksHandler2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class VPfragment extends Fragment {
    private String curdate;
    private final int page;

    private TasksHandler2 db;
    private TaskAdapter2 taskAdapter;
    private final MainActivity activity;


    private RecyclerView taskRecyclerList;
      private List<TaskModel2> taskList;
    private TextView curdayTV;

    public VPfragment(int curPage, MainActivity activity) {
        this.page = curPage;
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new TasksHandler2(activity);
        db.openDB();
        taskList = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        int difference = page - VPadapter.defaultpage;
        c.add(Calendar.DATE, difference);

        curdate = new SimpleDateFormat("yyMMdd").format(c.getTime());

        taskAdapter = new TaskAdapter2(db, activity, curdate);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.a, container, false);

        taskRecyclerList = rootView.findViewById(R.id.ataskRecyclerList);
        curdayTV = rootView.findViewById(R.id.curday);
        taskRecyclerList.setAdapter(taskAdapter);
        curdayTV = rootView.findViewById(R.id.curday);
        curdayTV.setText(curdate);

        onResume();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        taskList = db.getAllTasksForDay(curdate);
        Collections.reverse(taskList);
        taskAdapter.setTasks(taskList);
        taskAdapter.notifyDataSetChanged();
    }
}
