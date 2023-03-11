package com.example.czyjatomelodia.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Player;
import com.example.czyjatomelodia.PlayerViewHolder;
import com.example.czyjatomelodia.R;

import java.util.ArrayList;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerViewHolder> {


    Context context;
    ArrayList<Player> items;


    public PlayerAdapter(Context context, ArrayList<Player> items) {
        this.context = context;
        this.items = items;
    }



    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_item, parent,false);


        return new PlayerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {

        Player p = items.get(position);
        holder.playerName.setText(p.getName());

        if(position==0) {
            holder.linearLayout.setBackgroundColor(0xFFFF0000);
            holder.playerName.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_music_note_24,
                    0,
                    0,
                    0);
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
