package com.example.czyjatomelodia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaySongActivity extends YouTubeBaseActivity {

    YouTubePlayerView youTubePlayerView;
    String songID,nick,songTitle,playerName;
    TextView test,testowy;
    Button back;
   // private FirebaseDatabase database = FirebaseDatabase.getInstance("https://czyjatomelodia-f4d18-default-rtdb.europe-west1.firebasedatabase.app/");
    String TAG = "PLAYSONGACTIVITY________: ";
    DatabaseReference fReference;
    FirebaseDatabase fDatabase;
    FirebaseAuth fAuth;
    String hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        Intent intent = getIntent();
        songID=intent.getStringExtra("songID");
        songTitle=intent.getStringExtra("songTitle");
        testowy=findViewById(R.id.testowy);
        back=findViewById(R.id.btnBack);
        nick=intent.getStringExtra("nick");
        playerName = songTitle;
        test=findViewById(R.id.test);
        test.setText(songID);
        youTubePlayerView=findViewById(R.id.youtube_player_view);

        filterString();



        YouTubePlayer.OnInitializedListener listener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(songID);

            }


            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(PlaySongActivity.this,youTubeInitializationResult.toString(), Toast.LENGTH_LONG).show();

            }
        };

      //  String videoId = "qAHMCZBwYo4";
       // Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
       // startActivity(intent2);

        youTubePlayerView.initialize("AIzaSyC2Eg7NOATbUVFBjmlru8SrPm-Uw76dmo4",listener);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   onBackPressed(view);
                youTubePlayerView.initialize("AIzaSyC2Eg7NOATbUVFBjmlru8SrPm-Uw76dmo4",listener);
            }
        });

       // FirebaseManager firebaseManager = FirebaseManager.getInstance();

        changeStatus();

/*
        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance("https://czyjatomelodia-f4d18-default-rtdb.europe-west1.firebasedatabase.app/");
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        fReference =  fDatabase.getReference().child("Users").child(uid);
*/













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
                DatabaseReference roomRef = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(nickname+"1");
                Map<String, Object> gameInfo = new HashMap<>();
                gameInfo.put("Status", "playing");
                roomRef.updateChildren(gameInfo);
                String roomPath = nickname+"1";
                getPlayersFromFirebase(roomRef,roomPath);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG,"Wrong path");
            }
        });






      //  createRound(roomRef);
    }

    private void getPlayersFromFirebase(DatabaseReference roomReference,String roomPath) {
        DatabaseReference playersRef = roomReference.child("Players");
        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Pobranie listy graczy z bazy
                List<String> players = new ArrayList<>();
                for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                    String playerName = playerSnapshot.getKey();
                    players.add(playerName);
                }

                DatabaseReference roomRef = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomPath);
                createRound(roomRef,players);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Obsługa błędu, jeśli wystąpi
            }
        });
    }

    private void createRound(DatabaseReference roomReference,List<String> players ) {
        DatabaseReference roundRef = roomReference.child("Round1");
        Map<String, Object> gameInfo = new HashMap<>();
        gameInfo.put("Current", songTitle);
        gameInfo.put("Owner", playerName);

        // Tworzenie pól z wartością "null" dla każdego gracza w bazie
        DatabaseReference playersRef = roundRef.child("PlayerPicks");
        for (String player : players) {
            playersRef.child(player).setValue("null");
        }

        roundRef.updateChildren(gameInfo);
    }

    public void onBackPressed(View view) {

        finish(); // zamyka bieżącą aktywność
    }
}