package com.example.czyjatomelodia;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlayerViewHolder extends RecyclerView.ViewHolder{


    public TextView playerName;
    public LinearLayout linearLayout;

    public PlayerViewHolder(@NonNull View itemView) {
        super(itemView);


        playerName = itemView.findViewById(R.id.playerNameTv);
        linearLayout = itemView.findViewById(R.id.playerLinearLayout);

    }
}
