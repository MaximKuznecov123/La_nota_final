package com.La_nota.ALLA.Models;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.La_nota.ALLA.Activities.MainActivity;
import com.La_nota.ALLA.Adapters.TaskAdapter2;
import com.La_nota.ALLA.Adapters.VPadapter;
import com.La_nota.ALLA.R;
import com.La_nota.ALLA.Utils.TasksHandler2;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


public class VPfragment extends Fragment {
    private String curdate;
    private final int page;

    private TasksHandler2 db;
    private TaskAdapter2 basicAdapter;
    private TaskAdapter2 shAdapter;


    private RecyclerView basicListRV;
    private RecyclerView sharedListRV;

    private TextView curdayTV;
    boolean isNewTaskCreated = true;


    public VPfragment(int curPage) {
        this.page = curPage;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new TasksHandler2(VPadapter.getActivity());
        db.openDB();

        int difference = page - VPadapter.defaultpage;
        LocalDate date = LocalDate.now().plusDays(difference);

        curdate = MainActivity.formatter.format(date);
        basicAdapter = new TaskAdapter2(db);
        shAdapter = new TaskAdapter2(db, curdate);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.vpfragment, container, false);

        basicListRV = rootView.findViewById(R.id.ataskRecyclerList);
        basicListRV.setAdapter(basicAdapter);

        sharedListRV = rootView.findViewById(R.id.taskSHlist);
        sharedListRV.setAdapter(shAdapter);

        RefreshRVs();
        isNewTaskCreated = false;

        curdayTV = rootView.findViewById(R.id.curday);
        curdayTV = rootView.findViewById(R.id.curday);
        curdayTV.setText(MyDateformat(curdate, VPadapter.getActivity()));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNewTaskCreated)
            RefreshRVs();

        isNewTaskCreated = true;
    }

    public void RefreshRVs() {
        Thread thread = new Thread(() -> {
            List<TaskModel> shList = db.getTasks(curdate, true);
            Collections.reverse(shList);
            shAdapter.setTasks(shList);
        });
        thread.start();

        List<TaskModel> basicList = db.getTasks(curdate, false);
        Collections.reverse(basicList);
        basicAdapter.setTasks(basicList);

        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("MYLOG_threadException", e.getMessage() + '\n', e.getCause());
        }

        basicAdapter.notifyDataSetChanged();
        shAdapter.notifyDataSetChanged();
    }


    public static String MyDateformat(String date, MainActivity activity) {
        String month = date.substring(2, 4);
        String day = date.substring(4, 6);

        String s = "";
        switch (month) {
            case "01":
                s = activity.getResources().getString(R.string.january);
                break;
            case "02":
                s = activity.getResources().getString(R.string.february);
                break;
            case "03":
                s = activity.getResources().getString(R.string.march);
                break;
            case "04":
                s = activity.getResources().getString(R.string.april);
                break;
            case "05":
                s = activity.getResources().getString(R.string.may);
                break;
            case "06":
                s = activity.getResources().getString(R.string.june);
                break;
            case "07":
                s = activity.getResources().getString(R.string.july);
                break;
            case "08":
                s = activity.getResources().getString(R.string.august);
                break;
            case "09":
                s = activity.getResources().getString(R.string.september);
                break;
            case "10":
                s = activity.getResources().getString(R.string.october);
                break;
            case "11":
                s = activity.getResources().getString(R.string.november);
                break;
            case "12":
                s = activity.getResources().getString(R.string.december);
                break;
        }
        if (day.startsWith("0"))
            s += " " + day.replace("0", "");
        else
            s += " " + day;

        return s;
    }

}
