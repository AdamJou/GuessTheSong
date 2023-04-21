package com.example.czyjatomelodia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Adapter.PlayerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class VoteActivity extends AppCompatActivity {

    String curretPlayerNickname,roomID;
    String previousSong="null";
    String selectedPlayerNickname ="null";
    TextView songTitleTv,roundNumberTv;
    RecyclerView recyclerView;
    PlayerAdapter playerAdapter;
    ArrayList<Player> items;
    InfoDialog infoDialog;
    Button confirmPickBtn;
    public boolean isDataLoaded = false;
    public boolean isClicked = false;
    private static final String TAG = "GLOSOWANSKOOOOOO: ";
    private int selectedPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        Intent intent = getIntent();
        curretPlayerNickname=intent.getStringExtra("nickname");
        roomID=intent.getStringExtra("roomID");

        songTitleTv=findViewById(R.id.tvSongTitle);
        roundNumberTv=findViewById(R.id.tvRoundNumber);
        confirmPickBtn=findViewById(R.id.confirmPickBtn);


        infoDialog = new InfoDialog(VoteActivity.this);
        recyclerView = findViewById(R.id.playerList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        items = new ArrayList<>();
        playerAdapter = new PlayerAdapter(this,items,recyclerView);
        recyclerView.setAdapter(playerAdapter);

        DatabaseReference playersRef = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID).child("Players");

        //WCZYTYWANIE GRACZY RECYCLE VIEW
        playersRef.addValueEventListener(new ValueEventListener() {
            HashSet<String> userNames = new HashSet<>();


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isDataLoaded=false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String name = dataSnapshot.getKey();
                    if (!userNames.contains(name) ) {
                        //&& !name.equals(curretPlayerNickname)
                        // sprawdzamy, czy użytkownik już istnieje na liście
                        userNames.add(name);
                        Player player = new Player(name);
                        items.add(player);

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
        //

        //AKTUALIZACJA STANU GRY
        DatabaseReference statusRef = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID).child("Status");

        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                if (status.equals("playing")) {
                    infoDialog.closeDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Obsługa błędów

            }
        });

        //


        playerAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isClicked){
                    selectedPlayerNickname = items.get(position).getName();

                    Toast.makeText(VoteActivity.this, selectedPlayerNickname, Toast.LENGTH_LONG).show();

                    // Zmiana koloru klikniętego elementu na czerwony
                    playerAdapter.setBackgroundForPosition(selectedPosition, false); // Przywrócenie poprzedniemu elementowi domyślnego koloru
                    playerAdapter.setBackgroundForPosition(position, true); // Ustawienie klikniętemu elementowi czerwonego koloru
                    selectedPosition = position; // Aktualizacja pozycji klikniętego elementu

                    confirmPickBtn.setVisibility(View.VISIBLE);
                    confirmPickBtn.setEnabled(true);
                }else{
                    Toast.makeText(VoteActivity.this, "Nie możesz juz zmienic swojego wyboru!", Toast.LENGTH_LONG).show();

                }

            }
        });

        // POBIERANIE WARTOSCI CURRENT

        DatabaseReference roundRef =
                FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID).child("Round1").child("Current");

        roundRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currentValue = dataSnapshot.getValue(String.class);

                    if(!currentValue.equals(previousSong)){
                        refreshUI();
                    }
                    previousSong=currentValue;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FirebaseManager", "Błąd odczytu wartości z pola 'Current': " + databaseError.getMessage());
            }
        });


        confirmPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClicked=true;
                check(selectedPlayerNickname);
              //  playerAdapter.setOnItemClickListener(null);
                confirmPickBtn.setEnabled(false);
            }
        });


        infoDialog.loadDialog();
    }

    private void refreshUI() {

        DatabaseReference currentRef =
                FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID).child("Round1").child("Current");



        currentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String value = dataSnapshot.getValue(String.class);
                    songTitleTv.setText(value);
                    roundNumberTv.setText("Runda 1");
                  //  updateRecyclerView();
                    confirmPickBtn.setVisibility(View.INVISIBLE);
                    isClicked=false;
                } else {

                    Log.d("Firebase", "Brak wartości w bazie danych");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d("Firebase", "Błąd odczytu danych z Firebase: " + databaseError.getMessage());
            }
        });




    }

    private void check(String pick) {


        DatabaseReference checkRef =
                FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID).child("Round1").child("Owner");

        checkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currentValue = dataSnapshot.getValue(String.class);
                  if(pick.equals(currentValue)){

                      DatabaseReference sumRef =
                              FirebaseManager.getInstance().getDatabaseReference().child("Rooms")
                                      .child(roomID).
                                      child("Players").child(curretPlayerNickname).child("Score");


                      sumRef.addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(DataSnapshot dataSnapshot) {
                              if (dataSnapshot.exists()) {
                                  Integer score = dataSnapshot.getValue(Integer.class); // Pobranie wartości jako Integer
                                  if (score != null) {
                                      score++; // Inkrementacja wartości
                                      sumRef.setValue(score); // Aktualizacja wartości w bazie danych
                                  } else {
                                      // Obsługa przypadku, gdy wartość w bazie danych jest null
                                      Log.d("FirebaseManager", "Wartość w bazie danych jest null");
                                  }

                              }
                          }

                          @Override
                          public void onCancelled(DatabaseError databaseError) {
                              Log.d("FirebaseManager", "Błąd odczytu wartości z pola 'Current': " + databaseError.getMessage());
                          }
                      });


                  }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FirebaseManager", "Błąd odczytu wartości z pola 'Current': " + databaseError.getMessage());
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

}