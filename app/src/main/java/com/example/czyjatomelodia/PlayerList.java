package com.example.czyjatomelodia;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Adapter.PlayerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class PlayerList<isDataLoaded> extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference fReference;
    PlayerAdapter playerAdapter;
    ArrayList<Player> items;
    LinearLayout linearLayout;
    Button startGame,refresh;
    String isAdmin,id,nickname;
    TextView roomKey,roomName;
    private static final String TAG = "PlayerList";
    public boolean isDataLoaded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        Intent intent = getIntent();
        nickname=intent.getStringExtra("nickname");
         id = intent.getStringExtra("roomID");
         isAdmin = intent.getStringExtra("isAdmin");
        Toast.makeText(PlayerList.this, isAdmin, Toast.LENGTH_LONG).show();


        refresh=findViewById(R.id.refreshBtn);
        startGame = findViewById(R.id.startGameBtn);
        roomName=findViewById(R.id.tvRoomName);
        roomKey=findViewById(R.id.tvRoomKey);

        initializeText();
        buttonVisibility();




        linearLayout = findViewById(R.id.playerLinearLayout);
        recyclerView = findViewById(R.id.playerList);
        fReference = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(id).child("Players");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        items = new ArrayList<>();
        playerAdapter = new PlayerAdapter(this,items,recyclerView);
        recyclerView.setAdapter(playerAdapter);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRecyclerView();
            }
        });



        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               changeStatus();
            }
        });

        //Fill recycle
        fReference.addValueEventListener(new ValueEventListener() {
            HashSet<String> userNames = new HashSet<>();

            int iterator=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isDataLoaded=false;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String name = dataSnapshot.getKey();
                    DataSnapshot admin= dataSnapshot.child("isAdmin");
                    if (!userNames.contains(name)) { // sprawdzamy, czy użytkownik już istnieje na liście
                        userNames.add(name);
                        Player player = new Player(name);
                        items.add(player);

                        if (admin.toString().equals("DataSnapshot { key = isAdmin, value = true }")) {
                            playerAdapter.setBackgroundForPosition(iterator,true);

                        }else{
                            iterator++;
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


        //Change game status, start Searching
        fReference = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(id).child("Status");
        fReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                if (status != null && status.equals("running")) {

                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class).putExtra("nickname",nickname)
                            .putExtra("isAdmin",isAdmin).putExtra("id",id);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Obsługa błędów
                Log.e(TAG, "Failed to read value.", databaseError.toException());
            }
        });




    }

    private void initializeText() {

        SpannableString txt = new SpannableString("Pokój gracza: " + id.substring(0, id.length() - 1));
        txt.setSpan(new StyleSpan(Typeface.BOLD), 0, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        roomName.setText(txt);


        SpannableString txt2 = new SpannableString("KOD: " + id);
        txt2.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        roomKey.setText(txt2);


    }

    private void updateRecyclerView() {
        notifyAdapterIfNeeded();
    }

    private void notifyAdapterIfNeeded() {
        if (isDataLoaded) {
            playerAdapter.notifyDataSetChanged();
        }
    }
    private void changeStatus() {

        fReference = FirebaseDatabase.getInstance("https://czyjatomelodia-f4d18-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Rooms").child(id).child("Status");

        fReference.setValue("running")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Powodzenie - wartość została zaktualizowana
                        Log.d(TAG, "Status changed to running");
                        Toast.makeText(PlayerList.this, "JUPI", Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Błąd - nie udało się zaktualizować wartości
                        Log.e(TAG, "Failed to change status", e);
                    }
                });


    }

    private void buttonVisibility() {
        if(isAdmin.equals("true")) {

            startGame.setVisibility(View.VISIBLE);
        }

    }


}