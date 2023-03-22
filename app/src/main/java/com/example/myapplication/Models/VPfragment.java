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
        taskRecyclerList.setAdapter(taskAdapter);

        curdayTV = rootView.findViewById(R.id.curday);
        curdayTV = rootView.findViewById(R.id.curday);
        curdayTV.setText(MyDateformat(curdate, activity));

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

    public static String MyDateformat(String date, MainActivity activity){
        String month = date.substring(2, 4);
        String day = date.substring(4, 6);

        String s = "";
        switch (month){
            case "01": s = activity.getResources().getString(R.string.january);
            break;
            case "02": s = activity.getResources().getString(R.string.february);
                break;
            case "03": s = activity.getResources().getString(R.string.march);
                break;
            case "04": s = activity.getResources().getString(R.string.april);
                break;
            case "05": s = activity.getResources().getString(R.string.may);
                break;
            case "06": s = activity.getResources().getString(R.string.june);
                break;
            case "07": s = activity.getResources().getString(R.string.july);
                break;
            case "08": s = activity.getResources().getString(R.string.august);
                break;
            case "09": s = activity.getResources().getString(R.string.september);
                break;
            case "10": s = activity.getResources().getString(R.string.october);
                break;
            case "11": s = activity.getResources().getString(R.string.november);
                break;
            case "12": s = activity.getResources().getString(R.string.december);
                break;
        }
        if (day.startsWith("0"))
            s += " " + day.replace("0","");
        else
            s += " " + day;


        return s;
    }
}
