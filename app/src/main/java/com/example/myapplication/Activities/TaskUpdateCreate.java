package com.example.myapplication.Activities;

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

import com.example.myapplication.AbstractClasses.MyActivity;
import com.example.myapplication.Dialogs.EditTaskRepetition;
import com.example.myapplication.Models.SharedTaskModel;
import com.example.myapplication.R;
import com.example.myapplication.Models.BasicTaskModel;
import com.example.myapplication.Utils.TasksHandler2;


public class TaskUpdateCreate extends MyActivity {
    //Для получения данных из интента
    public static final String BundleExtra = "BundleExtra";
        //для получения данных из bundle
        public static final String BundleTaskName = "name";
        public static final String BundleTaskDescr = "descr";

    public static final String DateExtra = "DateExtra";
    public static final String PositionExtra = "PositionExtra";


    private EditText taskED, descriptionED;
    private TextView repetitionTV;

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
        repetitionTV = findViewById(R.id.task_repetition);

        Intent i = getIntent();
        Bundle bundle = i.getBundleExtra(BundleExtra);
        date = i.getStringExtra(DateExtra);

        db = new TasksHandler2(this);
        db.openDB();

        if(bundle != null){
            isUpd = true;

            taskED.setText(bundle.getString(BundleTaskName));
            descriptionED.setText(bundle.getString(BundleTaskDescr));

            position = i.getIntExtra(PositionExtra, 0);
        }
        EditTaskRepetition repDialog = new EditTaskRepetition(repeType);
        FragmentManager manager = getSupportFragmentManager();

        repetitionTV.setOnClickListener((view)->{
            repDialog.show(manager, "dialog");
        });
    }

    private void onCreateTask(String date) {
        String s = String.valueOf(taskED.getText());
        if(s.equals("")){
            Toast.makeText(this, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show();
        }else{
            if(repeType == 0) {
                BasicTaskModel newtask = new BasicTaskModel();
                newtask.setTask(s);
                newtask.setDescription(String.valueOf(descriptionED.getText()));
                newtask.setStatus(0);

                db.insertTask(newtask, date);
                finish();
            }else{
                //Log.d("MYLOG", "repetcreate");
                SharedTaskModel newtask = new SharedTaskModel();
                newtask.setAll(repeType, s, String.valueOf(descriptionED.getText()));
                db.insertSHTask(newtask, date);
                finish();
            }
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

    public void onDialogClick(int repeType){
        this.repeType = repeType;
        switch (repeType){
            case 1:
                repetitionTV.setText("Everyday");
                break;
            default:
                repetitionTV.setText("No repetition");
                break;
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
            case R.id.create:
                if (isUpd){
                    onUpdateTask(date, position);
                }else onCreateTask(date);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}