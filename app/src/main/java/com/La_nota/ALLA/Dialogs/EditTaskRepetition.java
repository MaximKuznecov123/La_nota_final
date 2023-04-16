package com.La_nota.ALLA.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.La_nota.ALLA.Activities.TaskUpdateCreate;

public class EditTaskRepetition extends DialogFragment {

    int repeType;

    public EditTaskRepetition(int repeType){
        this.repeType = repeType;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final String[] Array = {"Без повтора", "Каждый день"};

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
