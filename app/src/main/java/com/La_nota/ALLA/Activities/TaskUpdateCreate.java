package com.La_nota.ALLA.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.La_nota.ALLA.AbstractClasses.MyActivity;
import com.La_nota.ALLA.Dialogs.EditTaskRepetition;
import com.La_nota.ALLA.Models.BasicTaskModel;
import com.La_nota.ALLA.Models.SharedTaskModel;
import com.La_nota.ALLA.R;
import com.La_nota.ALLA.Utils.TasksHandler2;

import java.util.List;


public class TaskUpdateCreate extends MyActivity {
    //Для получения данных из интента
    public static final String BUNDLE_EXTRA = "BundleExtra";
    //для получения данных из bundle
    public static final String BUNDLE_TASK_NAME = "name";
    public static final String BUNDLE_TASK_DESCR = "descr";

    public static final String DATE_EXTRA = "DateExtra";
    public static final String POSITION_EXTRA = "PositionExtra";

    private EditText taskED, descriptionED;

    private TasksHandler2 db;

    String date;
    int position;

    Boolean isUpd = false;
    int repeType = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createtask);

        taskED = findViewById(R.id.Name);
        descriptionED = findViewById(R.id.description);

        db = new TasksHandler2(this);
        db.openDB();

        Intent i = getIntent();
        Bundle bundle = i.getBundleExtra(BUNDLE_EXTRA);

        date = i.getStringExtra(DATE_EXTRA);
        position = i.getIntExtra(POSITION_EXTRA, 0);

        if(bundle != null){
            isUpd = true;

            taskED.setText(bundle.getString(BUNDLE_TASK_NAME));
            descriptionED.setText(bundle.getString(BUNDLE_TASK_DESCR));


        }
    }

    private void onCreateTask(String date, int pos) {
        String s = String.valueOf(taskED.getText());
        if(s.equals("")){
            Toast.makeText(this, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show();
        }else{
                BasicTaskModel newtask = new BasicTaskModel();
                newtask.setTask(s);
                newtask.setDescription(String.valueOf(descriptionED.getText()));
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
            db.updateDescr(date, position, descriptionED.getText() + "");
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete:
                db.deleteTask(date, position);
                finish();
                break;
            case R.id.create:
                if (isUpd) onUpdateTask(date, position);
                else onCreateTask(date, position);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}