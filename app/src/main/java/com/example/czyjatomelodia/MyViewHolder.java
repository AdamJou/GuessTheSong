package com.example.czyjatomelodia;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {



    public ImageView imageView;
    public TextView textView;
    public RelativeLayout cardBg;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);


        imageView = (ImageView) itemView.findViewById(R.id.imageView);

        textView = itemView.findViewById(R.id.title);
       // uid = itemView.findViewById(R.id.description);
        cardBg = itemView.findViewById(R.id.cardBg);

    }


}
