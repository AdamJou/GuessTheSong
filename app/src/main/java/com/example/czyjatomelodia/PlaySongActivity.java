package com.example.czyjatomelodia;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Adapter.PlayerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class PlaySongActivity extends AppCompatActivity implements LifecycleObserver {

    YouTubePlayerView youTubePlayerView;
    String songID, nick, songTitle, playerName, roomID;
    RecyclerView playerPicksRecycle;
    DatabaseReference fReference;
    Button back;
    LinearLayout playerLinearLayout;
    PlayerAdapter playerAdapter;
    ArrayList<Player> playersVote;
    int roundNumber;
    String TAG = "PLAYSONGACTIVITY________: ";
    boolean finished = false;
    public boolean isDataLoaded = false;
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        Intent intent = getIntent();
        songID = intent.getStringExtra("songID");
        songTitle = intent.getStringExtra("songTitle");
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        back = findViewById(R.id.btnBack);
        nick = intent.getStringExtra("nick");
        playerName = songTitle;
        roomID = intent.getStringExtra("roomID");
        playerLinearLayout = findViewById(R.id.playerLinearLayout);
        playerPicksRecycle = findViewById(R.id.playerPicksRecycle);
        filterString();
        initializeAdapter();
        initializePlayer();


        //Listening for players who voted
        DatabaseReference setDrawableRef =
                FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID)
                        .child("Players");

        setDrawableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {


                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String playerName = dataSnapshot.getKey();
                    String voted = dataSnapshot.child("selected").getValue(String.class);

                    if (voted.equals("true")) {

                        for (Player player : playersVote) {
                            if (player.getName().equals(playerName)) {

                                // player.setCorrect(true);
                                //player.setBackgroundColor(0xFF00FF00);


                                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.ic_baseline_check_circle_outline_24);  // Pobranie Drawable z zasobów
                                player.setDrawable(drawable);  // Ustawienie Drawable dla gracza


                            }
                        }

                    }


                }

                updateRecyclerView();
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                // obsługa błędu
            }
        });


        FirebaseManager.getInstance().getNickname("Users", new FirebaseManager.OnNicknameCallback() {
            @Override
            public void onSuccess(String nickname) {

            }

            @Override
            public void onError(String errorMessage) {

            }
        });


        changeStatus();


        FirebaseManager.getInstance().getNumberOfRounds(roomID, new FirebaseManager.OnNumberOfRoundsCallback() {
            @Override
            public void onSuccess(int numberOfRounds) {
                roundNumber = numberOfRounds;

                DatabaseReference statusRef = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID).child("Status");

                statusRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        String status = snapshot.getValue(String.class);
                        if (status != null && status.equals("selected")) {

                            Log.e(TAG, "STATUS " + status);
                            back.setVisibility(View.VISIBLE);


                        }
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                    }
                });


            }


            @Override
            public void onError(String errorMessage) {

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseManager.getInstance().checkVotesForOwner(roomID, new FirebaseManager.OnCheckVotesForOwnerCallback() {
                    @Override
                    public void onVotesChecked(boolean additionalPointAdded) {
                        if (additionalPointAdded) {
                            Log.e(TAG, "DODANO PUNKCIK ESSA");
                        } else {
                            Log.e(TAG, "NIKOMU SIE NIE UDALO ;P");
                        }

                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Wystąpił błąd podczas sprawdzania głosów
                        // Wykonaj odpowiednie działania w przypadku błędu
                    }
                });

                finish();


            }
        });


    }

    private void initializePlayer() {
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {

                if (!songID.isEmpty()) {
                    youTubePlayer.loadVideo(songID, 0);
                }
            }
        });
    }

    private void initializeAdapter() {


        playerPicksRecycle.setHasFixedSize(true);
        playerPicksRecycle.setLayoutManager(new LinearLayoutManager(this));
        playersVote = new ArrayList<>();
        playerAdapter = new PlayerAdapter(this, playersVote, playerPicksRecycle);
        playerPicksRecycle.setAdapter(playerAdapter);


        fReference = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID).child("Players");

        fReference.addValueEventListener(new ValueEventListener() {
            HashSet<String> userNames = new HashSet<>();

            int iterator = 0;

            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                isDataLoaded = false;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String name = dataSnapshot.getKey();
                    String admin = dataSnapshot.child("isAdmin").getValue(String.class);
                    if (!userNames.contains(name)) { // sprawdzamy, czy użytkownik już istnieje na liście


                        if (!admin.equals("true")) {
                            userNames.add(name);
                            Player player = new Player(name, iterator);
                            playersVote.add(player);
                            //playerAdapter.setBackgroundForPosition(iterator,true);
                            iterator++;
                        }

                    }
                }
                isDataLoaded = true;
                updateRecyclerView();
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                // obsługa błędu
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void releaseYouTubePlayer() {
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.release();
    }

    private void updateRecyclerView() {
        notifyAdapterIfNeeded();
    }

    private void notifyAdapterIfNeeded() {
        if (isDataLoaded) {
            playerAdapter.notifyDataSetChanged();
        }
    }

    private void filterString() {

        String input = songTitle;
        String[] parts = input.split(" : ");

        if (parts.length > 1) {
            String nickname = parts[0].trim();
            String title = parts[1].trim();

            playerName = nickname;
            songTitle = title;
        } else {
            System.out.println("Niepoprawny format danych wejściowych.");
        }

    }

    private void changeStatus() {

        FirebaseManager.getInstance().getNickname("Users", new FirebaseManager.OnNicknameCallback() {
            @Override
            public void onSuccess(String nickname) {
                DatabaseReference roomRef = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID);
                Map<String, Object> gameInfo = new HashMap<>();
                gameInfo.put("Status", "playing");
                gameInfo.put("Current", songTitle);
                roomRef.updateChildren(gameInfo);
                String roomPath = roomID;
                getPlayersFromFirebase(roomRef, roomPath);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Wrong path");
            }
        });


        //  createRound(roomRef);
    }

    private void getPlayersFromFirebase(DatabaseReference roomReference, String roomPath) {
        DatabaseReference playersRef = roomReference.child("Players");
        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Pobranie listy graczy z bazy

                FirebaseManager.getInstance().getCurrentAdminNickname(roomID, new FirebaseManager.OnNicknameCallback() {
                    @Override
                    public void onSuccess(String nickname) {
                        List<String> players = new ArrayList<>();
                        for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                            String playerName = playerSnapshot.getKey();
                            if (!playerName.equals(nickname)) {
                                players.add(playerName);
                            }

                        }

                        DatabaseReference roomRef = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomPath);
                        createRound(roomRef, players);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Obsługa błędów
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Obsługa błędu, jeśli wystąpi
            }
        });
    }

    public String removeLastChar(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return str.substring(0, str.length() - 1);
    }

    private void createRound(DatabaseReference roomReference, List<String> players) {


        FirebaseManager.getInstance().getNumberOfRounds(roomID, new FirebaseManager.OnNumberOfRoundsCallback() {
            @Override
            public void onSuccess(int numberOfRounds) {
                // Log.e(TAG,"NUMEREK  " + numberOfRounds);

                DatabaseReference roundRef = roomReference.child("Round" + numberOfRounds);
                Map<String, Object> gameInfo = new HashMap<>();
                gameInfo.put("PlayedSong", songTitle);
                gameInfo.put("Owner", playerName);

                // Tworzenie pól z wartością "null" dla każdego gracza w bazie
                DatabaseReference playersRef = roundRef.child("PlayerPicks");
                for (String player : players) {
                    playersRef.child(player).setValue("null");
                }

                roundRef.updateChildren(gameInfo);
                finished = true;
            }

            @Override
            public void onError(String errorMessage) {
                // obsługa błędu
            }
        });


    }


    @Override
    public void onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }


    }


}
