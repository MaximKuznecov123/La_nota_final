package com.La_nota.ALLA.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.La_nota.ALLA.Activities.TaskUpdateCreate;
import com.La_nota.ALLA.R;

import java.util.Arrays;

public class EditTaskFrequency extends DialogFragment {
    FragmentActivity activity;
    int frequency;

    public EditTaskFrequency(int frequency) {
        this.frequency = frequency;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        activity = getActivity();

        String[] repArray;
        if (TaskUpdateCreate.repArray[4] == null)
            repArray = Arrays.copyOfRange(TaskUpdateCreate.repArray, 0, 4);
        else
            repArray = TaskUpdateCreate.repArray;

        int repeType;
        switch (frequency) {
            case 1:
            case 0:
                repeType = frequency;
                break;

            case 7:
                repeType = 2;
                break;

            default:
                repeType = 4;
                break;
        }

        builder.setTitle(getString(R.string.repeat))
                .setSingleChoiceItems(repArray, repeType,
                        (dialog, item) -> {
                            if (item == 3) {
                                dismiss();
                                makeOwnTaskFrequency();
                            } else {
                                switch (item) {
                                    case 0:
                                    case 1:
                                        frequency = item;
                                        break;
                                    case 2:
                                        frequency = 7;
                                        break;
                                }
                                ((TaskUpdateCreate) getActivity()).onDialogClick(frequency);
                                dismiss();
                            }

                        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_border);

        return dialog;
    }

    public void makeOwnTaskFrequency() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_own_task_frequency, null);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        view.findViewById(R.id.cancel).setOnClickListener((v -> {
            dialog.dismiss();
        }));

        view.findViewById(R.id.ok).setOnClickListener(v -> {
            String s = String.valueOf(((EditText) view.findViewById(R.id.dialog_custom_repeat_interval_value)).getText());
            if (s.equals("")) {
                Toast.makeText(activity, R.string.field + R.string.cant_be_empty, Toast.LENGTH_SHORT).show();
            } else {
                int value = Integer.parseInt(s);
                ((TaskUpdateCreate) activity).onDialogClick(value);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}