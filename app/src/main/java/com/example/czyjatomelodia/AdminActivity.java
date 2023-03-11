package com.example.czyjatomelodia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Adapter.PlayerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashSet;

public class AdminActivity extends AppCompatActivity {

    Button start, refresh;
    RecyclerView recyclerView;
    DatabaseReference fReference;
    PlayerViewHolder viewHolder;
    PlayerAdapter playerAdapter;
    ArrayList<Player> items;
    Integer last;

    String isAdmin, roomID, nickname;
    private static final String TAG = "ADMIN: ";
    public boolean isDataLoaded = false;
    public boolean everyoneSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        start = findViewById(R.id.startBtn);
        recyclerView = findViewById(R.id.playerSongList);
        refresh = findViewById(R.id.refreshBtn);
        Intent intent = getIntent();

        nickname = intent.getStringExtra("nickname");
        roomID = intent.getStringExtra("roomID");
        isAdmin = intent.getStringExtra("isAdmin");


        // Log.e(TAG,isAdmin);
        fReference = FirebaseDatabase.getInstance("https://czyjatomelodia-f4d18-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Rooms").child(roomID).child("Players");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        items = new ArrayList<>();
        playerAdapter = new PlayerAdapter(this, items, recyclerView);

        if (isAdmin.equals("true")) {
            playerAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // przekazanie tytułu piosenki do metody onItemClick
                    String ytSongID = items.get(position).getSongID();
                    String ytSongTitle =items.get(position).getName();
                    // uruchomienie nowej aktywności i przekazanie tytułu
                    Intent intent = new Intent(AdminActivity.this, PlaySongActivity.class);
                    intent.putExtra("songID", ytSongID);
                    intent.putExtra("nick", nickname);
                    intent.putExtra("songTitle", ytSongTitle);
                    //filtorwanie listy
                    items.get(position).setSongID("played");
                    last=position;

                    startActivity(intent);
                }
            });
        }


        recyclerView.setAdapter(playerAdapter);


        fReference.addValueEventListener(new ValueEventListener() {
            HashSet<String> userNames = new HashSet<>();


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isDataLoaded = false;
             //   Integer playerCount =0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    String name = dataSnapshot.getKey();
                    DataSnapshot song = dataSnapshot.child("songName");
                    DataSnapshot ytID = dataSnapshot.child("songID");
                    DataSnapshot admin = dataSnapshot.child("isAdmin");
                    name = name + " : " + song.getValue().toString();
                    String songID = ytID.getValue().toString();
                    if (!userNames.contains(name)) { // sprawdzamy, czy użytkownik już istnieje na liście
                        userNames.add(name);
                        Player player = new Player(name,songID);
                        Log.i(TAG, player.getSongID());
                        if(!songID.equals("null"))
                        {
                            items.add(player);
                            //playerCount++;
                        }




                        if (admin.toString().equals("DataSnapshot { key = isAdmin, value = true }")) {
                            int index = items.size() - 1;
                            setBackgroundForPlayer(index, 0xFaFa0000);

                        }



                    }

                }
                isDataLoaded = true;
                updateRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // obsługa błędu
            }


        });

    }



    private void updateRecyclerView() {
        notifyAdapterIfNeeded();
    }

    private void notifyAdapterIfNeeded() {
        if (isDataLoaded) {
            playerAdapter.notifyDataSetChanged();
        }
    }


    public void setBackgroundForPlayer(int position, int color) {
        Player p = items.get(position);
        p.setBackgroundColor(color);
        playerAdapter.notifyItemChanged(position);
    }




    @Override
    protected void onPause() {
        super.onPause();
        // Aktualizacja danych w adapterze
        ArrayList<Player> filteredItems = new ArrayList<>();
        for (Player item : items) {
            if (!item.getSongID().equals("played")) {
                filteredItems.add(item);
            }
        }
        items.clear();
        items.addAll(filteredItems);
        // Wywołanie metody notifyDataSetChanged() w wątku UI
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
    }



}
