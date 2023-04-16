package com.La_nota.ALLA.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.La_nota.ALLA.Models.TaskModel;
import com.La_nota.ALLA.R;
import com.La_nota.ALLA.Utils.TasksHandler2;


public class TaskUpdateCreate extends AppCompatActivity {
    //Для получения данных из интента
    public static final String BUNDLE_EXTRA = "bundle";

    public static final String TASK_NAME_EXTRA = "name";

    public static final String DATE_EXTRA = "DateExtra";
    public static final String POSITION_EXTRA = "PositionExtra";
    public static final String IS_UPD_EXTRA = "isUpd";


    private EditText taskED;
    private Button deleteBT, createBT;

    private TasksHandler2 db;


    private Boolean isUpd;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_createtask);
        }catch (Exception e){
            Log.d("MYLOG", e.getMessage());
        }

        setTitle("");
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        taskED = findViewById(R.id.nametask);
        createBT = findViewById(R.id.create);
        deleteBT = findViewById(R.id.delete);

        Intent i = getIntent();
        Bundle data = i.getBundleExtra(BUNDLE_EXTRA);

        String date = data.getString(DATE_EXTRA);
        int position = data.getInt(POSITION_EXTRA);
        isUpd = i.getBooleanExtra(IS_UPD_EXTRA, false);

        db = new TasksHandler2(this);
        db.openDB();

        if (!isUpd) {
            deleteBT.setVisibility(View.GONE);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) createBT.getLayoutParams();
            params.endToEnd = R.id.nametask;
            params.startToStart = R.id.nametask;
            params.width = 0;

            createBT.setLayoutParams(params);
            createBT.setOnClickListener(v -> onCreateTask(date, position));
        } else {
            createBT.setText("Update");
            taskED.setText(data.getString(TASK_NAME_EXTRA));
            createBT.setOnClickListener((v -> onUpdateTask(date, position)));
            deleteBT.setOnClickListener((v -> onDeleteTask(date, position)));
        }
    }

    private void onCreateTask(String date, int pos) {
        String s = String.valueOf(taskED.getText());
        if(s.equals("")){
            Toast.makeText(this, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show();
        }else{
                TaskModel newtask = new TaskModel();
                newtask.setTask(s);
                newtask.setStatus(0);

                db.insertTask(newtask, date, pos);
                finish();
        }
    }

    private void onUpdateTask(String date, int position){
        String s = String.valueOf(taskED.getText());
        if(s.equals("")){
            Toast.makeText(this, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show();
        }else {
            db.updateTask(date, position, s);
            finish();
        }
    }

    int a = 1;
    private void onDeleteTask(String date, int position){
        if(a == 2){
            db.deleteTask(date, position);
            finish();
        }else {
            a++;
            Toast.makeText(this, "Repeat to delete", Toast.LENGTH_SHORT).show();
        }

    }

}