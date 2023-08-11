package com.example.czyjatomelodia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Adapter.PlayerAdapter;
import com.example.czyjatomelodia.Base.BaseActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class AdminActivity extends BaseActivity {

    Button refresh, end;
    RecyclerView recyclerView;
    DatabaseReference fReference;
    PlayerAdapter playerAdapter;
    ArrayList<Player> items;
    Integer last;
    TextView adminInfo;
    int playersCount;
    String isAdmin, roomID, nickname;
    private static final String TAG = "ADMIN: ";
    public boolean isDataLoaded = false;
    private ProgressDialog progressDialog;
    private Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        recyclerView = findViewById(R.id.playerSongList);
        refresh = findViewById(R.id.refreshBtn);
        end = findViewById(R.id.btnEnd);
        adminInfo = findViewById(R.id.tvAdminInfo);
        Intent intent = getIntent();

        nickname = intent.getStringExtra("nickname");
        roomID = intent.getStringExtra("roomID");
        isAdmin = intent.getStringExtra("isAdmin");


        fReference = FirebaseDatabase.getInstance("https://czyjatomelodia-f4d18-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Rooms").child(roomID).child("Players");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        items = new ArrayList<>();
        playerAdapter = new PlayerAdapter(AdminActivity.this, items, recyclerView);


        // Sprawdzenie, czy wszyscy gracze mają ustawione wartości songID i songName


        progressDialog = new ProgressDialog(AdminActivity.this);
        progressDialog.setMessage("Ładowanie..");
        progressDialog.setCancelable(false);
        uiHandler = new Handler(Looper.getMainLooper());

        showDialog();
        if (isAdmin.equals("true")) {
            DatabaseReference playersRef = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID).child("Players");
            playersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                    int playerCount = (int) dataSnapshot.getChildrenCount();
                    int readyPlayerCount = 0;

                    for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                        String songID = playerSnapshot.child("songID").getValue(String.class);
                        String songName = playerSnapshot.child("songName").getValue(String.class);
                        if (!songID.equals("null") && !songName.equals("null")) {
                            readyPlayerCount++;
                        }
                    }
                    if (playerCount == readyPlayerCount) {
                        dismissDialog();
                        playerAdapter.setOnItemClickListener((parent, view, position, id) -> {
                            // przekazanie tytułu piosenki do metody onItemClick
                            FirebaseManager.getInstance().incrementNumberOfRounds(roomID);
                            String ytSongID = items.get(position).getSongID();
                            String ytSongTitle = items.get(position).getName();
                            // uruchomienie nowej aktywności i przekazanie tytułu
                            Intent intent1 = new Intent(AdminActivity.this, PlaySongActivity.class);
                            intent1.putExtra("songID", ytSongID);
                            intent1.putExtra("nick", nickname);
                            intent1.putExtra("songTitle", ytSongTitle);
                            intent1.putExtra("roomID", roomID);

                            //filtorwanie listy
                            items.get(position).setSongID("played");
                            last = position;
                            FirebaseManager.getInstance().setUnselectedForNonAdminPlayers(roomID);
                            FirebaseManager.getInstance().clearSongDataForAllPlayers(roomID);
                            FirebaseManager.getInstance().setPlayingStatusInRoom(roomID, "playing");
                            playerAdapter.clearSelection();
                            startActivity(intent1);

                        });
                    }
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                    // Obsługa błędów
                }
            });

        }


        recyclerView.setAdapter(playerAdapter);

        //recycle view setup
        fReference.addValueEventListener(new ValueEventListener() {
            final HashSet<String> userNames = new HashSet<>();


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isDataLoaded = false;
                //   Integer playerCount =0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    String name = dataSnapshot.getKey();
                    DataSnapshot song = dataSnapshot.child("songName");
                    DataSnapshot ytID = dataSnapshot.child("songID");
                    name = name + " : " + song.getValue().toString();
                    String songID = ytID.getValue().toString();
                    if (!userNames.contains(name)) { // sprawdzamy, czy użytkownik już istnieje na liście
                        userNames.add(name);
                        Player player = new Player(name, songID);
                        Log.i(TAG, player.getSongID());
                        if (!songID.equals("null")) {
                            items.add(player);
                            //playerCount++;
                        }



/*
                        if (admin.toString().equals("DataSnapshot { key = isAdmin, value = true }")) {
                            int index = items.size() - 1;
                            setBackgroundForPlayer(index, 0xFaFa0000);

                        }
*/


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


        FirebaseManager.getInstance().getNumberOfPlayersInRoom(roomID, new FirebaseManager.OnNumberOfPlayersCallback() {
            @Override
            public void onSuccess(int numberOfPlayers) {
                playersCount = numberOfPlayers;
            }

            @Override
            public void onError(String errorMessage) {
                // Handle the error
            }
        });


        //Round end


        end.setOnClickListener(v -> {
            //FirebaseManager.getInstance().setNumberOfRounds(roomID);
            FirebaseManager.getInstance().setPlayingStatusInRoom(roomID, "finished");
            FirebaseManager.getInstance().assignDJ(roomID, djId -> FirebaseManager.getInstance()
                    .checkIfCurrentUserIsAdmin(
                            roomID, isAdmin -> FirebaseManager.getInstance().setPlayingStatusInRoom(roomID, "waiting")));


            Intent intent12 = new Intent(AdminActivity.this, ResultActivity.class);
            intent12.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent12.putExtra("roomID", roomID);
            startActivity(intent12);
            finish();


        });


    }


    private void showDialog() {
        progressDialog.show();
    }

    private void dismissDialog() {
        progressDialog.dismiss();
    }

    private void updateRecyclerView() {
        notifyAdapterIfNeeded();
    }

    private void notifyAdapterIfNeeded() {
        if (isDataLoaded) {
            playerAdapter.notifyDataSetChanged();


        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseManager.getInstance().getNumberOfRounds(roomID, new FirebaseManager.OnNumberOfRoundsCallback() {
            @Override
            public void onSuccess(int numberOfRounds) {
                if (numberOfRounds == playersCount) {
                    end.setVisibility(View.VISIBLE);
                    adminInfo.setText("Brak utworów do odtworzenia.");

                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

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
        runOnUiThread(() -> playerAdapter.notifyDataSetChanged());


        FirebaseManager.getInstance().getNumberOfRounds(roomID, new FirebaseManager.OnNumberOfRoundsCallback() {
            @Override
            public void onSuccess(int numberOfRounds) {
                Log.e(TAG, String.valueOf(numberOfRounds));
                if (numberOfRounds == playersCount) {
                    end.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });


    }


}
