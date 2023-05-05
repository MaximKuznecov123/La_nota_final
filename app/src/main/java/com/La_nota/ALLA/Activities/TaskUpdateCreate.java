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

import com.La_nota.ALLA.Database.TasksHandler2;
import com.La_nota.ALLA.Dialogs.EditTaskFrequency;
import com.La_nota.ALLA.Models.TaskModel;
import com.La_nota.ALLA.R;


public class TaskUpdateCreate extends AppCompatActivity {
    //Для получения данных из интента
    public static final String BUNDLE_EXTRA = "bundle";

    public static final String TASK_TITLE_EXTRA = "name";
    public static final String TASK_DESCR_EXTRA = "descr";

    public static final String DATE_EXTRA = "DateExtra";
    public static final String ID_EXTRA = "IDExtra";
    public static final String FREQUENCY_EXTRA = "FrExtra";

    public static String[] repArray;

    private EditText titleED, descrED;
    private Button deleteBT, createBT;
    private TextView repetitionTV;

    int frequency = 0;

    private TasksHandler2 db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_createtask);
        setTitle("");
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        repArray = new String[]{getResources().getString(R.string.no_repetition), getResources().getString(R.string.everyday), getResources().getString(R.string.every_week), getResources().getString(R.string.choose_own), null};

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
            repetitionTV.setVisibility(View.GONE);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) createBT.getLayoutParams();
            params.topToBottom = R.id.descr;
            params.endToEnd = R.id.descr;
            createBT.setLayoutParams(params);

            params = (ConstraintLayout.LayoutParams) deleteBT.getLayoutParams();
            params.startToStart = R.id.descr;
            params.topToBottom = R.id.descr;
            deleteBT.setLayoutParams(params);

            createBT.setText(R.string.update);
            titleED.setText(data.getString(TASK_TITLE_EXTRA));
            descrED.setText(data.getString(TASK_DESCR_EXTRA));
            frequency = data.getInt(FREQUENCY_EXTRA, 0);

            int id = data.getInt(ID_EXTRA);
            createBT.setOnClickListener((v -> onUpdateTask(id)));
            deleteBT.setOnClickListener((v -> onDeleteTask(id)));
        }

        EditTaskFrequency repDialog = new EditTaskFrequency(frequency);
        FragmentManager manager = getSupportFragmentManager();
        repetitionTV.setText(repArray[frequency]);

        repetitionTV.setOnClickListener((view) -> {
            repDialog.show(manager, "dialog");
        });
    }

    private void onCreateTask(String date) {
        String s = String.valueOf(titleED.getText());
        if (s.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.title) + getResources().getString(R.string.cant_be_empty), Toast.LENGTH_SHORT).show();
        } else {
            TaskModel newtask = new TaskModel();
            newtask.setTitle(s);
            newtask.setDescription(String.valueOf(descrED.getText()));
            newtask.setStatus(0);
            newtask.setFrequency(frequency);

            db.insertTask(newtask, date);
            finish();
        }
    }

    private void onUpdateTask(int id) {
        String s = String.valueOf(titleED.getText());
        if (s.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.title) + getResources().getString(R.string.cant_be_empty), Toast.LENGTH_SHORT).show();
        } else {
            db.updateTask(id, s, frequency != 0);
            db.updateDescr(id, String.valueOf(descrED.getText()), frequency != 0);
            finish();
        }
    }

    int toDeleteCount = 1;

    private void onDeleteTask(int id) {
        if (toDeleteCount == 2) {
            db.deleteTask(id, frequency != 0);
            finish();
        } else {
            toDeleteCount++;
            Toast.makeText(this, R.string.repeat_to_delete, Toast.LENGTH_SHORT).show();
        }
    }

    public void onFrequencyDialogClick(int frequency) {
        this.frequency = frequency;
        switch (frequency) {
            case 0:
            case 1:
                repetitionTV.setText(repArray[frequency]);
                repArray[4] = null;
                break;
            case 7:
                repetitionTV.setText(repArray[2]);
                repArray[4] = null;
                break;
            default:
                if (repArray[4] == null) {
                    repArray[4] = getResources().getString(R.string.once_in_days) + frequency + getResources().getString(frequency > 4 ? R.string.dnei : R.string.dnia);
                    repetitionTV.setText(repArray[4]);
                }
                break;
        }
    }
}