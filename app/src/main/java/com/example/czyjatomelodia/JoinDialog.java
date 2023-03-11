package com.example.czyjatomelodia;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class JoinDialog extends AppCompatDialogFragment {
    private EditText roomID;
    private JoinDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.join_dialog, null);

        builder.setView(view).setTitle("Dołącz do gry").setNegativeButton("Odrzuć", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("Zatwierdź", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String id = roomID.getText().toString();
                listener.applyID(id);
            }
        });
        roomID = view.findViewById(R.id.joinEt);
        return  builder.create();
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
