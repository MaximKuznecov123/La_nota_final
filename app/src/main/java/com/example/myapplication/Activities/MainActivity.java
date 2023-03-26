package com.example.myapplication.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.AbstractClasses.MyActivity;
import com.example.myapplication.R;
import com.example.myapplication.Adapters.VPadapter;
import com.example.myapplication.Utils.TasksHandler2;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends MyActivity {
    private static final String date = "date";
    static SharedPreferences sh;

    private static ViewPager2 viewPager;
    private static ExtendedFloatingActionButton fab;
    private static VPadapter VPadaptor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Calendar c = Calendar.getInstance();
        sh = getPreferences(MODE_PRIVATE);
        tableClearer(c);

        viewPager = findViewById(R.id.viewPager);
        VPadaptor = new VPadapter(this);
        //viewPager.registerOnPageChangeCallback(new VPadapter.PageListener());
        viewPager.setAdapter(VPadaptor);
        viewPager.setCurrentItem(VPadapter.defaultpage,false);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            int difference = viewPager.getCurrentItem() - VPadapter.defaultpage;
            c.add(Calendar.DATE, difference);
            String date = new SimpleDateFormat("yyMMdd").format(c.getTime());

            Intent i = new Intent(this, TaskUpdateCreate.class);
            i.putExtra(TaskUpdateCreate.DateExtra,date);
            startActivity(i);
            c.add(Calendar.DATE, -difference);
        });
    }

    private void tableClearer(Calendar c){

        String today = new SimpleDateFormat("yyMMdd").format(c.getTime());

        if (sh.contains(date)){
            TasksHandler2 db = new TasksHandler2(this);
            db.openDB();
            //db.deleteAll();
            if(sh.getString(date,"").equals(today)) {
                return;
            }
            else {
                db.clearTable(c);
            }
        }

        SharedPreferences.Editor edit = sh.edit();
        edit.putString(date, today);
        edit.apply();
    }
}