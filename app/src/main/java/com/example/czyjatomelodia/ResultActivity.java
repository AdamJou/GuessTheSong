package com.example.czyjatomelodia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Adapter.PlayerAdapter;
import com.example.czyjatomelodia.Base.BaseActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends BaseActivity {

    TextView songTitle, songOwner, tvRound, tvReady;
    String roomID, title, owner, admin;
    PlayerAdapter playerAdapter;
    RecyclerView recyclerView;
    ArrayList<Player> items;
    ImageButton ready;
    Button next, previous, scoreboard;
    int current = 1;
    int nor = Integer.MAX_VALUE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        scoreboard = findViewById(R.id.btnScoreboard);
        next = findViewById(R.id.btnNext);
        tvReady = findViewById(R.id.tvReady);
        previous = findViewById(R.id.btnPrevious);
        ready = findViewById(R.id.btnReady);
        songOwner = findViewById(R.id.tvRoundOwnerInfo);
        tvRound = findViewById(R.id.tvRoundNumber);
        recyclerView = findViewById(R.id.resultRecycle);
        songTitle = findViewById(R.id.tvRoundSongInfo);
        Intent intent = getIntent();
        roomID = intent.getStringExtra("roomID");
        //roomID = "g1";

        FirebaseManager.getInstance().checkIfCurrentUserIsAdmin(roomID, isAdmin -> {

            admin = isAdmin;
            FirebaseManager.getInstance().setPlayingStatusInRoom(roomID, "waiting");
        });


        FirebaseManager.getInstance().checkIfAllPlayersWereDJs(roomID, () -> {

            tvReady.setText(R.string.endgame);
            ready.setOnClickListener(v -> {
                Intent intent1 = new Intent(ResultActivity.this, HomeActivity.class);
                startActivity(intent1);
                finish();
            });
        });

        getRoundNumber();
        updateButtons(current, nor);
        setRecycleViewHeight();
        setResults();


        scoreboard.setOnClickListener(v -> FirebaseManager.getInstance().getAllPlayersWithPoints(roomID, new FirebaseManager.OnPlayersWithPointsCallback() {
            @Override
            public void onSuccess(List<Player> playersWithPoints) {

                ScoreboardDialog.show(ResultActivity.this, playersWithPoints);
            }

            @Override
            public void onError(String errorMessage) {
                // Tutaj możesz obsłużyć ewentualne błędy
            }
        }));


        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        firebaseManager.startListeningForPlayerChanges(roomID, () -> firebaseManager.getNickname("Users", new FirebaseManager.OnNicknameCallback() {
            @Override
            public void onSuccess(String nickname) {
                FirebaseManager.getInstance().setNumberOfRounds(roomID);
                FirebaseManager.getInstance().setCurrentSong(roomID);

                firebaseManager.deleteRoundNodes(roomID);
                endRound(nickname);
            }

            @Override
            public void onError(String errorMessage) {

            }
        }));


        next.setOnClickListener(v -> {
            current++;
            setResults();

            updateButtons(current, nor);


        });

        previous.setOnClickListener(v -> {
            --current;

            setResults();
            updateButtons(current, nor);


        });

        ready.setOnClickListener(v -> {

            FirebaseManager.getInstance().checkIfAllPlayersWereDJs(roomID, () -> {

                tvReady.setText("KONIEC GRY! Kiknij aby przejsc do menu!");
                ready.setOnClickListener(v1 -> {
                    Intent intent12 = new Intent(ResultActivity.this, HomeActivity.class);
                    startActivity(intent12);
                    finish();
                });
            });


            ready.getDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            ready.setClickable(false);
            tvReady.setText("Potwierdzono gotowość");
            FirebaseManager.getInstance().getNickname("Users", new FirebaseManager.OnNicknameCallback() {
                @Override
                public void onSuccess(String nickname) {
                    FirebaseManager.getInstance().setReady(roomID, nickname);
                }

                @Override
                public void onError(String errorMessage) {

                }
            });

        });


    }

    private void endRound(String nickname) {
        Intent intent = new Intent(ResultActivity.this, SearchActivity.class).putExtra("nickname", nickname)
                .putExtra("isAdmin", admin).putExtra("id", roomID);
        startActivity(intent);
        finish();
    }


    private void setRecycleViewHeight() {


        FirebaseManager.getInstance().getNumberOfPlayersInRoom(roomID, new FirebaseManager.OnNumberOfPlayersCallback() {
            @Override
            public void onSuccess(int numberOfPlayers) {
/*
                ViewGroup.LayoutParams params=recyclerView.getLayoutParams();
                params.height= numberOfPlayers * 136;
                Log.e("HEIGHT ", String.valueOf(params.height))
                ;
                recyclerView.setLayoutParams(params);
*/


            }

            @Override
            public void onError(String errorMessage) {
                // Handle the error
            }
        });


    }

    private int getRoundNumber() {
        FirebaseManager.getInstance().getNumberOfRounds(roomID, new FirebaseManager.OnNumberOfRoundsCallback() {
            @Override
            public void onSuccess(int numberOfRounds) {

                nor = numberOfRounds;
                updateButtons(current, nor);


            }

            @Override
            public void onError(String errorMessage) {
                nor = -1;
            }
        });
        return nor;
    }


    private void updateButtons(int current, int numberOfRounds) {
        if (current == 1) {
            previous.setEnabled(false);
        } else {
            previous.setEnabled(true);
        }

        next.setEnabled(current != numberOfRounds);
    }

    private void setResults() {

        Log.e("CURRENT ", String.valueOf(current));
        if (current <= nor) {
            setRoundNumber(current);
            setSongTitle(current);
            setSongOwner(current);
            setRecycleView(current);

        }


    }


    private void setRoundNumber(int current) {


        ObjectAnimator animator = ObjectAnimator.ofFloat(tvRound, "alpha", 1.0f, 0.0f);


        animator.setDuration(1000);


        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Zmiana tekstu i ustawienie właściwości alpha na 1.0f
                tvRound.setText("Runda " + current);
                tvRound.setAlpha(1.0f);
            }
        });


        animator.start();


    }

    private void setRecycleView(int nor) {


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        items = new ArrayList<>();
        playerAdapter = new PlayerAdapter(this, items, recyclerView);
        recyclerView.setAdapter(playerAdapter);

        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        controller.setDelay(0.5f);
        recyclerView.setLayoutAnimation(controller);
        DatabaseReference playerPicksRef = FirebaseManager.getInstance().getDatabaseReference()
                .child("Rooms").child(roomID).child("Round" + nor).child("PlayerPicks");

        playerPicksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    items.clear();
                    for (DataSnapshot playerSnapshot : snapshot.getChildren()) {
                        String playerName = playerSnapshot.getKey();
                        String playerVote = playerSnapshot.getValue(String.class);

                        playerName = playerName + " → " + playerVote;

                        Player player = new Player(playerName);


                        if (!playerVote.isEmpty() && !owner.isEmpty() && owner.equals(playerVote)) {
                            player.setCorrect(true);
                            player.setBackgroundColor(0xFF00FF00);
                            playerAdapter.notifyDataSetChanged();

                        }

                        items.add(player);
                    }

                    playerAdapter.notifyDataSetChanged();
                    recyclerView.scheduleLayoutAnimation();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setSongOwner(int nor) {
        DatabaseReference songRef = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID).child("Round" + nor)
                .child("Owner");

        songRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    owner = snapshot.getValue(String.class);


                    ObjectAnimator animator = ObjectAnimator.ofFloat(songOwner, "alpha", 1.0f, 0.0f);


                    animator.setDuration(1000);


                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // Zmiana tekstu i ustawienie właściwości alpha na 1.0f
                            songOwner.setText("Gracza: " + owner);
                            songOwner.setAlpha(1.0f);
                        }
                    });
                    animator.start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setSongTitle(int nor) {
        DatabaseReference titleRef = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID).child("Round" + nor)
                .child("PlayedSong");

        titleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    title = snapshot.getValue(String.class);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(songTitle, "alpha", 1.0f, 0.0f);
                    animator.setDuration(1000);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // Zmiana tekstu i ustawienie właściwości alpha na 1.0f
                            songTitle.setText("Utwór: " + title);
                            songTitle.setAlpha(1.0f);
                        }
                    });

                    animator.start();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}