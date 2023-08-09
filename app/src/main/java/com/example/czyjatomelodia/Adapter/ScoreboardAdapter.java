package com.example.czyjatomelodia.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Player;
import com.example.czyjatomelodia.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class ScoreboardAdapter extends RecyclerView.Adapter<ScoreboardAdapter.ViewHolder> {

    private List<Player> players;

    public ScoreboardAdapter(List<Player> players) {
        this.players = players;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView playerNameTextView;
        public TextView playerScoreTextView;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.player_name_textview);
            playerScoreTextView = itemView.findViewById(R.id.player_score_textview);
            linearLayout=itemView.findViewById(R.id.linearScoreboard);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scoreboard_row_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player player = players.get(position);
        holder.playerNameTextView.setText(player.getName());
        holder.playerScoreTextView.setText(String.valueOf(player.getPlayerID()));
        if(position%2==0){
            String hexColor = "#454545";
            int color = Color.parseColor(hexColor);
            holder.linearLayout.setBackgroundColor(color);
        }

    }

    @Override
    public int getItemCount() {
        return players.size();
    }
}
