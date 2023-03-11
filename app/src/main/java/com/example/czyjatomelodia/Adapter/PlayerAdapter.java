package com.example.czyjatomelodia.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Player;
import com.example.czyjatomelodia.PlayerViewHolder;
import com.example.czyjatomelodia.R;

import java.util.ArrayList;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerViewHolder> {


    private Context context;
    private ArrayList<Player> items;
    private RecyclerView recyclerView;
    private AdapterView.OnItemClickListener onItemClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public PlayerAdapter(Context context, ArrayList<Player> items, RecyclerView recyclerView ) {
        this.context = context;
        this.items = items;
        this.recyclerView = recyclerView;


    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }



    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_item, parent,false);


        return new PlayerViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player p = items.get(holder.getAdapterPosition());
        holder.playerName.setText(p.getName());


        if (holder.getAdapterPosition() == selectedPosition) {
            holder.linearLayout.setBackgroundColor(0xFFFF0000); // kolor czerwony

        } else {

            holder.linearLayout.setBackgroundColor(0xFFFFFFFF); // kolor biały

        }

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int previousSelectedPosition = selectedPosition;
                    selectedPosition = holder.getAdapterPosition();
                    notifyItemChanged(previousSelectedPosition);
                    notifyItemChanged(selectedPosition);
                    onItemClickListener.onItemClick(null, v, holder.getAdapterPosition(), 0);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setBackgroundForPosition(int position, boolean isSelected) {
        if (isSelected) {
            /*
            PlayerViewHolder holder = (PlayerViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            holder.linearLayout.setBackgroundColor(0xFFFF0000); // kolor czerwony
*/

            selectedPosition = position;
            notifyItemChanged(position);
        }



    }

}
