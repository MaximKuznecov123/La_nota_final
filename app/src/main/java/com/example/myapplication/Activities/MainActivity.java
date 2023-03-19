package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.AbstractClasses.MyActivity;
import com.example.myapplication.R;
import com.example.myapplication.Adapters.VPadapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends MyActivity {
    private static ViewPager2 viewPager;
    private static ExtendedFloatingActionButton fab;
    private static VPadapter VPadaptor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        viewPager = findViewById(R.id.viewPager);
        VPadaptor = new VPadapter(this);
        //viewPager.registerOnPageChangeCallback(new VPadapter.PageListener());
        viewPager.setAdapter(VPadaptor);
        viewPager.setCurrentItem(VPadapter.defaultpage,false);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, viewPager.getCurrentItem()-VPadapter.defaultpage);
            String date = new SimpleDateFormat("ddMMyy").format(c.getTime());

            Intent i = new Intent(this, TaskUpdateCreate.class);
            i.putExtra(TaskUpdateCreate.DateExtra,date);
            startActivity(i);
        });
    }
}