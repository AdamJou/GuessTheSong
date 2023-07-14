package com.example.czyjatomelodia;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class JoinDialog extends AppCompatDialogFragment {
    private EditText roomID;
    private JoinDialogListener listener;


    @Override
    public void onStart() {
        super.onStart();

       // Ustawienie tła dialogu na podstawie corners.xml
        Dialog dialog = getDialog();
        if (dialog != null) {

            dialog.getWindow().setBackgroundDrawableResource(R.drawable.corners);
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.join_dialog, null);

        builder.setView(view).setTitle("Dołącz do gry").setNegativeButton("Odrzuć", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle negative button click
            }
        }).setPositiveButton("Zatwierdź", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String id = roomID.getText().toString();
                listener.applyID(id);
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show(); //Only after .show() was called
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackground(getResources().getDrawable(R.drawable.cornersempty));
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) negativeButton.getLayoutParams();
        params.setMargins(0, 0, 20, 0);
        negativeButton.setLayoutParams(params);





        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackground(getResources().getDrawable(R.drawable.cornersempty));





        roomID = view.findViewById(R.id.joinEt);
       // return builder.create();
        return  dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (JoinDialogListener) context;
        } catch (ClassCastException e) {
           throw new ClassCastException(context.toString() + "Join Dialog is not allowed here");
        }
    }

    public interface JoinDialogListener{
        void applyID(String id);
    }


}
