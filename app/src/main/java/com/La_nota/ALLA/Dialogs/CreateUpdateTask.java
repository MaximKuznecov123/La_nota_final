package com.La_nota.ALLA.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.La_nota.ALLA.Activities.MainActivity;
import com.La_nota.ALLA.Models.TaskModel;
import com.La_nota.ALLA.Utils.TasksHandler2;

public class CreateUpdateTask extends DialogFragment {
    //для получения данных из bundle
    public static final String TASK_NAME_EXTRA = "name";

    public static final String DATE_EXTRA = "DateExtra";
    public static final String POSITION_EXTRA = "PositionExtra";
    private static final String IS_UPD_EXTRA = "isUpd";


    private EditText taskED;
    private Button deleteBT, createBT;

    private TasksHandler2 db;

    private String taskname;
    private String date;
    private int position;

    private Boolean isUpd;
    MainActivity activity;

    private AlertDialog builder;

    public CreateUpdateTask(MainActivity activity, Bundle data) {
        this.activity = activity;

        taskname = data.getString(TASK_NAME_EXTRA);
        date = data.getString(DATE_EXTRA);
        position = data.getInt(POSITION_EXTRA);
        isUpd = data.getBoolean(IS_UPD_EXTRA);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        builder = new AlertDialog.Builder(getActivity()).create();
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.create_task_dialog, null);
//        builder.setView(view);
//
//        taskED = view.findViewById(R.id.nametask);
//        createBT = view.findViewById(R.id.create);
//        deleteBT = view.findViewById(R.id.delete);
//        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        db = new TasksHandler2(activity);
//        db.openDB();
//
//        if (!isUpd) {
//            deleteBT.setVisibility(View.GONE);
//
//            createBT.setOnClickListener(v -> onCreateTask(date, position));
//        } else {
//            taskED.setText(taskname);
//            createBT.setOnClickListener((v -> onUpdateTask(date, position)));
//            deleteBT.setOnClickListener((v -> onDeleteTask(date, position)));
//        }
//
//
        return builder;
    }


    private void onCreateTask(String date, int pos) {
        String s = String.valueOf(taskED.getText());
        if (s.equals("")) {
            Toast.makeText(activity, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show();
        } else {
            TaskModel newtask = new TaskModel();
            newtask.setTask(s);
            newtask.setStatus(0);

            db.insertTask(newtask, date, pos);
            builder.cancel();
        }
    }

    private void onUpdateTask(String date, int position) {
        String s = String.valueOf(taskED.getText());
        if (s.equals("")) {
            Toast.makeText(activity, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show();
        } else {
            db.updateTask(date, position, s);
            builder.cancel();
        }
    }

    private void onDeleteTask(String date, int position){
        db.deleteTask(date, position);
    }

}
