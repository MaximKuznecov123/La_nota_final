package com.La_nota.ALLA.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.La_nota.ALLA.Dialogs.EditTaskFrequency;
import com.La_nota.ALLA.Models.TaskModel;
import com.La_nota.ALLA.R;
import com.La_nota.ALLA.Utils.TasksHandler2;


public class TaskUpdateCreate extends AppCompatActivity {
    //Для получения данных из интента
    public static final String BUNDLE_EXTRA = "bundle";

    public static final String TASK_TITLE_EXTRA = "name";
    public static final String TASK_DESCR_EXTRA = "descr";

    public static final String DATE_EXTRA = "DateExtra";
    public static final String ID_EXTRA = "IDExtra";
    public static final String FREQUENCY_EXTRA = "FrExtra";

    public static final String[] repArray = {"No repetition", "Every day", "Every week"};

    private EditText titleED, descrED;
    private Button deleteBT, createBT;
    private TextView repetitionTV;

    int repeType = 0;

    private TasksHandler2 db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_createtask);
        setTitle("");
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        titleED = findViewById(R.id.nametask);
        descrED = findViewById(R.id.descr);
        createBT = findViewById(R.id.create);
        deleteBT = findViewById(R.id.delete);
        repetitionTV = findViewById(R.id.repetitionTV);

        Intent i = getIntent();
        Bundle data = i.getBundleExtra(BUNDLE_EXTRA);

        db = new TasksHandler2(this);
        db.openDB();

        if (data == null) {
            deleteBT.setVisibility(View.GONE);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) createBT.getLayoutParams();
            params.endToEnd = R.id.descr;
            params.startToStart = R.id.descr;
            params.width = 0;
            createBT.setLayoutParams(params);

            String date = i.getStringExtra(DATE_EXTRA);
            createBT.setOnClickListener(v -> onCreateTask(date));
        } else {
            createBT.setText("Update");
            titleED.setText(data.getString(TASK_TITLE_EXTRA));
            descrED.setText(data.getString(TASK_DESCR_EXTRA));
            repeType = data.getInt(FREQUENCY_EXTRA, 0);

            int id = data.getInt(ID_EXTRA);
            createBT.setOnClickListener((v -> onUpdateTask(id)));
            deleteBT.setOnClickListener((v -> onDeleteTask(id)));
        }

        EditTaskFrequency repDialog = new EditTaskFrequency(repeType);
        FragmentManager manager = getSupportFragmentManager();
        repetitionTV.setText(repArray[repeType]);

        repetitionTV.setOnClickListener((view)->{
            repDialog.show(manager, "dialog");
        });
    }

    private void onCreateTask(String date) {
        String s = String.valueOf(titleED.getText());
        if(s.equals("")){
            Toast.makeText(this, "Label can't be empty", Toast.LENGTH_SHORT).show();
        }else{
                TaskModel newtask = new TaskModel();
                newtask.setTitle(s);
                newtask.setDescription(String.valueOf(descrED.getText()));
                newtask.setStatus(0);
                newtask.setFrequency(repeType);

                db.insertTask(newtask, date);
                finish();
        }
    }

    private void onUpdateTask(int id){
        String s = String.valueOf(titleED.getText());
        if(s.equals("")){
            Toast.makeText(this, "Label can't be empty", Toast.LENGTH_SHORT).show();
        }else {
            db.updateTask(id, s, repeType != 0);
            db.updateDescr(id, String.valueOf(descrED.getText()), repeType != 0);
            finish();
        }
    }

    int toDeleteCount = 1;
    private void onDeleteTask(int id){
        if(toDeleteCount == 2){
            db.deleteTask(id, repeType != 0);
            finish();
        }else {
            toDeleteCount++;
            Toast.makeText(this, "Repeat to delete", Toast.LENGTH_SHORT).show();
        }
    }

    public void onDialogClick(int repetype){
        this.repeType = repetype;
        repetitionTV.setText(repArray[repetype]);
    }

}