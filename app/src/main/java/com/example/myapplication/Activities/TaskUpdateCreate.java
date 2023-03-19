package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.AbstractClasses.MyActivity;
import com.example.myapplication.R;
import com.example.myapplication.Models.TaskModel2;
import com.example.myapplication.Utils.TasksHandler2;


public class TaskUpdateCreate extends MyActivity {
    //Для получения данных из интента
    public static final String BundleExtra = "BundleExtra";
        //для получения данных из bundle
        public static final String BundleTaskName = "name";
        public static final String BundleTaskDescr = "descr";

    public static final String DateExtra = "DateExtra";
    public static final String PositionExtra = "PositionExtra";



    private EditText task, description;

    private TasksHandler2 db;

    String date ;
    int position;

    Boolean isUpd = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createtask);

        task = findViewById(R.id.Name);
        description = findViewById(R.id.description);
        Button createTask = findViewById(R.id.taskCreate);

        Intent i = getIntent();
        Bundle bundle = i.getBundleExtra(BundleExtra);
        date = i.getStringExtra(DateExtra);

        db = new TasksHandler2(this);
        db.openDB();

        if(bundle != null){
            isUpd = true;
            String tasktext = bundle.getString("task");
            String descr = bundle.getString("descr");
            position = i.getIntExtra(PositionExtra, 0);

            task.setText(tasktext);
            description.setText(descr);
            createTask.setOnClickListener(view -> onUpdateTask(date, position));
        }else {
            createTask.setOnClickListener(view -> onCreateTask(date));
        }
    }

    private void onCreateTask(String date) {
        String s = String.valueOf(task.getText());
        if(s.equals("")){
            Toast.makeText(this, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show();
        }else{
            TaskModel2 newtask = new TaskModel2();
            newtask.setTask(s);
            newtask.setDescription(String.valueOf(description.getText()));
            newtask.setStatus(0);

            db.insertTask(newtask, date);
            finish();
        }
    }

    private void onUpdateTask(String date, int position){
        String s = String.valueOf(task.getText());
        if(s.equals("")){
            Toast.makeText(this, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show();
        }else {
            db.updateTask(date, position, s);
            db.updateDescr(date, position, description.getText() + "");
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menutask_upd_create,menu);
        //кнопка "удалить" появляется только при обновлении задания
        if(!isUpd) menu.removeItem(R.id.delete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete:
                db.deleteTask(date, position);
                finish();
                break;
            //будут ещё компоненты
        }
        return super.onOptionsItemSelected(item);
    }
}