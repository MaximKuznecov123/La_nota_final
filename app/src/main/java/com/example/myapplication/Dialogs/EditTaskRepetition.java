package com.example.myapplication.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.Activities.TaskUpdateCreate;

public class EditTaskRepetition extends DialogFragment {

    int repeType;
    public EditTaskRepetition(int repeType){
        this.repeType = repeType;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final String[] Array = {"No repetition", "Everyday"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Repeat")
                .setSingleChoiceItems(Array, repeType,
                        (dialog, item) -> {
                    repeType = item;
                    ((TaskUpdateCreate)getActivity()).onDialogClick(repeType);
                    dismiss();
                        });
        return builder.create();
    }

}
