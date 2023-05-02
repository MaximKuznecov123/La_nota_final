package com.La_nota.ALLA.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.La_nota.ALLA.Activities.TaskUpdateCreate;
import com.La_nota.ALLA.R;

public class EditTaskFrequency extends DialogFragment {
    int repeType;

    public EditTaskFrequency(int repeType){
        this.repeType = repeType;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Repeat")
                .setSingleChoiceItems(TaskUpdateCreate.repArray, repeType,
                        (dialog, item) -> {
                            repeType = item;
                            ((TaskUpdateCreate)getActivity()).onDialogClick(repeType);
                            dismiss();
                        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_border);

        return dialog;
    }
}