package com.La_nota.ALLA.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;
import com.La_nota.ALLA.Adapters.VPadapter;
import com.La_nota.ALLA.Dialogs.EditTaskFrequency;
import com.La_nota.ALLA.R;
import com.La_nota.ALLA.Utils.TasksHandler2;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class MainActivity extends AppCompatActivity {
    private static final String date = "date";
    private SharedPreferences sh;

    private ViewPager2 viewPager;
    private ExtendedFloatingActionButton fab;
    private VPadapter VPadaptor;

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main2);

        LocalDate localDate = LocalDate.now();
        sh = getPreferences(MODE_PRIVATE);

        tableClearer(localDate);


        viewPager = findViewById(R.id.viewPager);
        VPadaptor = new VPadapter(this);
        viewPager.setAdapter(VPadaptor);
        viewPager.setCurrentItem(VPadapter.defaultpage, false);
        viewPager.setOffscreenPageLimit(1);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            int difference = viewPager.getCurrentItem() - VPadapter.defaultpage;

            LocalDate curPageDate = localDate.plusDays(difference);
            String date = formatter.format(curPageDate);

            Intent i = new Intent(this, TaskUpdateCreate.class);
            i.putExtra(TaskUpdateCreate.DATE_EXTRA, date);

            startActivity(i);

        });
    }

    private void tableClearer(LocalDate date1) {

        String today = formatter.format(date1);

        if (sh.contains(date)) {
            TasksHandler2 db = new TasksHandler2(this);
            db.openDB();
            //db.deleteBASIC();

            if (sh.getString(date, "").equals(today)) {
                return;
            } else {
                LocalDate dateForClear = date1.plusDays(-VPadapter.defaultpage);
                db.clearTable(formatter.format(dateForClear));
            }
        }

        SharedPreferences.Editor edit = sh.edit();
        edit.putString(date, today);
        edit.apply();
    }
}