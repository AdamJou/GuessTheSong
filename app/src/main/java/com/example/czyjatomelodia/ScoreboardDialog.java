package com.example.czyjatomelodia;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Adapter.ScoreboardAdapter;

import java.util.List;
public class ScoreboardDialog extends Dialog {

    private RecyclerView recyclerView;
    private ScoreboardAdapter adapter;

    public ScoreboardDialog(@NonNull Context context, List<Player> players) {
        super(context);
        setContentView(R.layout.scoreboard_dialog_layout);

        recyclerView = findViewById(R.id.scoreboard_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new ScoreboardAdapter(players);
        recyclerView.setAdapter(adapter);
    }

    public static void show(Activity activity, List<Player> players) {
        ScoreboardDialog dialog = new ScoreboardDialog(activity, players);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }
}

