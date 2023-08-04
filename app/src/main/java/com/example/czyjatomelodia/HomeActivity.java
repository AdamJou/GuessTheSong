package com.example.czyjatomelodia;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.czyjatomelodia.Base.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends BaseActivity implements JoinDialog.JoinDialogListener {


    private MediaPlayer mediaPlayer;
    DatabaseReference fReference;
    FirebaseDatabase fDatabase;
    FirebaseAuth fAuth;
    ImageView logo;
    Button create, join, logout;
    TextView username;
    String nick;
    Animation fade, leftfade,rightfade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.usernameTxt);
        create = findViewById(R.id.createGameBtn);
        join = findViewById(R.id.joinGameBtn);
        logout = findViewById(R.id.btnLogout);
        logo=findViewById(R.id.logo);

        mediaPlayer = MediaPlayer.create(this, R.raw.intro);
      //  mediaPlayer.start();

        FirebaseUser user = fAuth.getCurrentUser();
        leftfade = AnimationUtils.loadAnimation(this, R.anim.leftfade);
        rightfade = AnimationUtils.loadAnimation(this, R.anim.rightfade);
        fade = AnimationUtils.loadAnimation(this, R.anim.fadeanim);
        //   username.setText(user.getUid());
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        //  fDatabase = FirebaseDatabase.getInstance();
        //  fReference=fDatabase.getReference("Users");
        //  fDatabase = FirebaseDatabase.getInstance("https://czyjatomelodia-f4d18-default-rtdb.europe-west1.firebasedatabase.app/");

     /*   final String[] xd = new String[1];
      .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DataSnapshot> task) {
              xd[0] = task.getResult().getValue().toString();
              username.setText(xd[0]);
           }
       });*/


        join.startAnimation(rightfade);
        create.startAnimation(leftfade);
        logout.startAnimation(fade);
        logo.startAnimation(fade);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });


        fReference = FirebaseDatabase.getInstance("https://czyjatomelodia-f4d18-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        fReference.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {

                    DataSnapshot snapshot = task.getResult();
                    nick = String.valueOf(snapshot.child("Nickname").getValue());
                    username.setText(nick);

                } else {
                    Toast.makeText(HomeActivity.this, "Wystąpił błąd", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //ADMIN
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://czyjatomelodia-f4d18-default-rtdb.europe-west1.firebasedatabase.app/");
                //Game Info
                DatabaseReference myRef = database.getReference("Rooms").child(nick + "1");
                Map<String, Object> gameInfo = new HashMap<>();
                gameInfo.put("Status", "pending");
                gameInfo.put("NumberOfRounds", 0);
                myRef.setValue(gameInfo);

                //Player info
                myRef = database.getReference("Rooms").child(nick + "1").child("Players").child(nick);


                Map<String, Object> playerInfo = new HashMap<>();
                playerInfo.put("Score", 0);
                playerInfo.put("isAdmin", "true");
                playerInfo.put("songID", "null");
                playerInfo.put("selected", "false");
                playerInfo.put("songName", "null");


                myRef.setValue(playerInfo);

                startActivity(new Intent(getApplicationContext(), PlayerList.class).putExtra("roomID", nick + 1)
                        .putExtra("isAdmin", "true").putExtra("nickname", nick));
                finish();


            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });


    }

    private void openDialog() {
        JoinDialog joinDialog = new JoinDialog();
        joinDialog.show(getSupportFragmentManager(), "Join dialog");

    }

    @Override
    public void applyID(String id) {

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://czyjatomelodia-f4d18-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("Rooms");
        final ArrayList<String> locationNames = new ArrayList<>();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    locationNames.add(ds.getKey());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myRef.addListenerForSingleValueEvent(eventListener);


        if (!id.isEmpty()) {


            myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {


                        DataSnapshot snapshot = task.getResult();


                        if (locationNames.contains(id)) {
                            Map<String, Object> playerInfo = new HashMap<>();
                            playerInfo.put("Score", 0);
                            playerInfo.put("isAdmin", "false");
                            playerInfo.put("songID", "null");
                            playerInfo.put("songName", "null");
                            playerInfo.put("selected", "false");

                            myRef.child(id).child("Players").child(nick).setValue(playerInfo);
                            try {
                                startActivity(new Intent(getApplicationContext(), PlayerList.class).putExtra("roomID", id)
                                        .putExtra("isAdmin", "false").putExtra("nickname", nick));
                                finish();

                            } catch (Exception e) {

                                Toast.makeText(HomeActivity.this, e.toString(), Toast.LENGTH_LONG).show();

                                e.printStackTrace();
                            }


                        } else {

                            Toast.makeText(HomeActivity.this, "Pokój o danej nazwie nie istnieje!", Toast.LENGTH_LONG).show();
                        }


                    } else {
                        Toast.makeText(HomeActivity.this, "Coś poszło nie tak", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

