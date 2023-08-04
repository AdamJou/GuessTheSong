package com.example.czyjatomelodia;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Adapter.PlayerAdapter;
import com.example.czyjatomelodia.Base.BaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class VoteActivity extends BaseActivity {

    private static final String TAG = "GLOSOWANSKOOOOOO: ";
    final String room = "Rooms";
    public boolean isDataLoaded = false;
    public boolean isClicked = false;
    String curretPlayerNickname, roomID;
    String previousSong = "null";
    int roundNumber = 1;
    String selectedPlayerNickname = "null";
    TextView songTitleTv, roundNumberTv, moj;
    RecyclerView recyclerView;
    PlayerAdapter playerAdapter;
    ArrayList<Player> items;
    InfoDialog infoDialog;
    Button confirmPickBtn;
    private int selectedPosition = RecyclerView.NO_POSITION;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        Intent intent = getIntent();

        FirebaseManager.getInstance().getNickname("Users", new FirebaseManager.OnNicknameCallback() {
            @Override
            public void onSuccess(String nickname) {
                curretPlayerNickname = nickname;
                Log.e(TAG, "CURRENT PLAYER NAME " + curretPlayerNickname);
                moj.setText(curretPlayerNickname);
                DatabaseReference playersRef = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID).child("Players");

                playersRef.addValueEventListener(new ValueEventListener() {
                    final HashSet<String> userNames = new HashSet<>();


                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isDataLoaded = false;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String name = dataSnapshot.getKey();
                            if (!userNames.contains(name) && !name.equals(curretPlayerNickname)) {
                                //&& !name.equals(curretPlayerNickname)
                                Log.e(TAG, "XD " + curretPlayerNickname);
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
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

        //   curretPlayerNickname=intent.getStringExtra("nickname");
        roomID = intent.getStringExtra("roomID");
        //  roomID="a1";
        songTitleTv = findViewById(R.id.tvSongTitle);
        roundNumberTv = findViewById(R.id.tvRoundNumber);
        confirmPickBtn = findViewById(R.id.confirmPickBtn);

        moj = findViewById(R.id.mojNick);
        infoDialog = new InfoDialog(VoteActivity.this);
        recyclerView = findViewById(R.id.playerList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        items = new ArrayList<>();
        playerAdapter = new PlayerAdapter(VoteActivity.this, items, recyclerView);
        recyclerView.setAdapter(playerAdapter);

        //  refreshUI();

        //WCZYTYWANIE GRACZY RECYCLE VIEW

        //


        //AKTUALIZACJA STANU GRY
        DatabaseReference statusRef = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID).child("Status");

        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                assert status != null;
                if (status.equals("playing")) {
                    infoDialog.closeDialog();
                    refreshUI();
                }
                if (status.equals("finished")) {
                    Intent intent = new Intent(VoteActivity.this, ResultActivity.class);
                    intent.putExtra("roomID", roomID);
                    //playerAdapter.setOnItemClickListener(null);
                    confirmPickBtn.setOnClickListener(null);
                    startActivity(intent);

                    finish();
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
                selectedPosition = -1;
                selectedPlayerNickname = "null";
                if (!isClicked) {
                    selectedPlayerNickname = items.get(position).getName();


                    playerAdapter.setBackgroundForPosition(selectedPosition, false); // Przywrócenie poprzedniemu elementowi domyślnego koloru
                    playerAdapter.setBackgroundForPosition(position, true); // Ustawienie klikniętemu elementowi czerwonego koloru
                    selectedPosition = position; // Aktualizacja pozycji klikniętego elementu

                    confirmPickBtn.setVisibility(View.VISIBLE);
                    confirmPickBtn.setEnabled(true);


                } else {
                    Toast.makeText(VoteActivity.this, "Nie możesz juz zmienic swojego wyboru!", Toast.LENGTH_LONG).show();

                }

            }
        });

        // POBIERANIE WARTOSCI CURRENT


        ValueEventListener roundListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                FirebaseManager.getInstance().getNumberOfRounds(roomID, new FirebaseManager.OnNumberOfRoundsCallback() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(int numberOfRounds) {
                        DatabaseReference roundRef = FirebaseManager.getInstance().getDatabaseReference().child(room).child(roomID).child("Current");
                        roundNumber = numberOfRounds;
                        roundNumberTv.setText("Runda " + roundNumber);
                        roundRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String currentValue = dataSnapshot.getValue(String.class);

                                    if (!currentValue.equals(previousSong)) {
                                        refreshUI();
                                    }
                                    previousSong = currentValue;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("FirebaseManager", "Błąd odczytu wartości z pola 'Current': " + databaseError.getMessage());
                            }
                        });

                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // obsłuż błąd, jeśli taki wystąpi
            }
        };


        //   DatabaseReference roundRef =
        FirebaseManager.getInstance().getDatabaseReference().child(room).child(roomID).
                child("Current").addValueEventListener(roundListener);


        confirmPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClicked = true;
                playerAdapter.clearSelection();
                check(selectedPlayerNickname);
                Log.e(TAG, "WYBRANY: " + selectedPlayerNickname);
                //  selectedPlayerNickname="null";

                Log.e(TAG, "MOJ: " + curretPlayerNickname);
                // playerAdapter.setOnItemClickListener(null);
                confirmPickBtn.setEnabled(false);
                playerAdapter.setBackgroundForPosition(selectedPosition, false);
            }
        });
        infoDialog.loadDialog();
    }


    private void refreshUI() {


        DatabaseReference currentRef =
                FirebaseManager.getInstance().getDatabaseReference().child(room).child(roomID).child("Current");


        currentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String value = dataSnapshot.getValue(String.class);
                    songTitleTv.setText(value);
                    roundNumberTv.setText("Runda " + roundNumber);
                    confirmPickBtn.setVisibility(View.INVISIBLE);
                    isClicked = false;
                    playerAdapter.setBackgroundForPosition(selectedPosition, false);
                    selectedPosition = RecyclerView.NO_POSITION;
                    updateRecyclerView();
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


        DatabaseReference setPickRef = FirebaseManager.getInstance().getDatabaseReference().child(room)
                .child(roomID).child("Round" + roundNumber).child("PlayerPicks").child(curretPlayerNickname);
        setPickRef.setValue(pick);

        DatabaseReference checkRef =
                FirebaseManager.getInstance().getDatabaseReference().child(room).child(roomID).child("Round" + roundNumber).child("Owner");

        checkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currentValue = dataSnapshot.getValue(String.class);
                    if (pick.equals(currentValue)) {


                        DatabaseReference sumRef =
                                FirebaseManager.getInstance().getDatabaseReference().child(room)
                                        .child(roomID).
                                        child("Players").child(curretPlayerNickname).child("Score");


                        sumRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    Integer score = dataSnapshot.getValue(Integer.class); // Pobranie wartości jako Integer
                                    if (score != null) {
                                        // score++; // Inkrementacja wartości
                                        // sumRef.setValue(score); // Aktualizacja wartości w bazie danych
                                        checkPlayer(pick, sumRef);

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

        /*






         */


        DatabaseReference setRef =
                FirebaseManager.getInstance().getDatabaseReference().child(room).child(roomID)
                        .child("Players").child(curretPlayerNickname).child("selected");

        setRef.setValue("true");

        everyPlayerVoted();
    }


    private void checkPlayer(String pick, DatabaseReference ref) {

        if (!pick.isEmpty()) {
            DatabaseReference setScoreRef =
                    FirebaseManager.getInstance().getDatabaseReference().child(room).child(roomID)
                            .child("Round" + roundNumber).child("PlayerPicks").child(curretPlayerNickname);

            Log.d(TAG, "numer: " + roundNumber);
            setScoreRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String currentPlayerPick = snapshot.getValue(String.class);

                        if (currentPlayerPick.equals(pick)) {

                            Log.d(TAG, "CZYZBY? " + currentPlayerPick.equals(pick));
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Integer score = snapshot.getValue(Integer.class);
                                    score++;
                                    DatabaseReference setPlayerScore =
                                            FirebaseManager.getInstance().getDatabaseReference().child(room).child(roomID)
                                                    .child("Players").child(curretPlayerNickname).child("Score");
                                    setPlayerScore.setValue(score);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }


    private void everyPlayerVoted() {

        FirebaseManager.getInstance().areAllPlayersSelected(roomID, new FirebaseManager.OnAllPlayersSelectedCallback() {
            @Override
            public void onSuccess(boolean allPlayersVoted) {
                if (allPlayersVoted) {
                    Log.d("TAG", "Wszyscy gracze w pokoju zagłosowali.");
                    DatabaseReference setRef =
                            FirebaseManager.getInstance().getDatabaseReference().child(room).child(roomID).child("Status");
                    setRef.setValue("selected")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Powodzenie - wartość została zaktualizowana
                                    Log.d(TAG, "Status changed to running");


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
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("TAG", errorMessage);
            }

            @Override
            public void onPlayerNotSelected(boolean notSelected) {
                if (!notSelected) {
                    Log.e("TAG", "JESZCZE NIE WSZYSCY ZAGLOSOWALI");
                }
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


   /* @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerAdapter != null) {
            // Usunięcie OnClickListener
            playerAdapter.setOnItemClickListener(null);
        }
    }*/
}