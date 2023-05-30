package com.example.czyjatomelodia;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

public class InfoDialog {

   private Activity activity;
   private AlertDialog dialog;

   InfoDialog(Activity myActivity) {
        activity=myActivity;


   }

    void loadDialog(){
       AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.info_dialog, null);

        builder.setView(view);

        builder.setCancelable(false);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }
    public void closeDialog(){
        if (dialog != null && dialog.isShowing()) { // sprawdzenie, czy dialog istnieje i jest widoczny
            dialog.dismiss();

        }
    }

}
