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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Adapter.VideoDetailsAdapter;
import com.example.czyjatomelodia.Model.Item;
import com.example.czyjatomelodia.Model.VideoDetails;
import com.example.czyjatomelodia.Network.NetworkInstance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity  {

        DatabaseReference fReference;
        TextView selectedSong;
        Button submit,ku,kd;
        FirebaseAuth fAuth;
        RecyclerView recyclerView;
        String nickname,isAdmin,roomID;
        SearchView search;


        private final String TAG = "INFORMACJE";
        private boolean isSearchExecuted = false;
        private ProgressDialog progressDialog;
        private Handler uiHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
       // btn = (Button)findViewById(R.id.btnYt);
      //  txt=(EditText) findViewById(R.id.ytTxt);
        fAuth = FirebaseAuth.getInstance();
       // username=findViewById(R.id.userUID);



        selectedSong=findViewById(R.id.tvSelectedSong);
        recyclerView = (RecyclerView)findViewById(R.id.ytRecycle);
        submit=(Button)findViewById(R.id.btnSubmitSong);
        search=(SearchView)findViewById(R.id.ytTxt);
        ku=findViewById(R.id.btnKurwa);
        kd=findViewById(R.id.btnKd);
        progressDialog = new ProgressDialog(SearchActivity.this);
        progressDialog.setMessage("Ładowanie..");
        progressDialog.setCancelable(false);
        uiHandler = new Handler(Looper.getMainLooper());



        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                                              @Override
                                              public boolean onQueryTextSubmit(String s) {
                                                    Search(s);
                                                  showDialog();
                                                  return true;
                                              }

                                          @Override
                                          public boolean onQueryTextChange(String newText) {
                                              return false;
                                          }
                                      });


        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        recyclerView.hasFixedSize();

        Intent intent = getIntent();
        nickname=intent.getStringExtra("nickname");



        isAdmin=intent.getStringExtra("isAdmin");
        roomID=intent.getStringExtra("id");
        FirebaseManager.getInstance().clearSongDataForAllPlayers(roomID);
        FirebaseUser user = fAuth.getCurrentUser();


        Log.e(TAG," CHECK IF ADMIN " + isAdmin);
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        //  username.setText(user.getUid());


        
        ku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fReference = FirebaseDatabase.getInstance("https://czyjatomelodia-f4d18-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("Rooms").child(roomID).child("Players").child(nickname);


                Map<String, Object> playerInfo = new HashMap<>();
                playerInfo.put("songID", "_JZom_gVfuw");
                playerInfo.put("songName", "The Notorious B.I.G. - Juicy (Official Video) [4K]");

                fReference.updateChildren(playerInfo);

                Toast.makeText(SearchActivity.this, nickname, Toast.LENGTH_SHORT).show();
                submit.setEnabled(false);

                selectedSong.setText("The Notorious B.I.G. - Juicy (Official Video) [4K]");
                if (isAdmin.equals("true")) {
                    Intent intent = new Intent(SearchActivity.this, AdminActivity.class).putExtra("nickname",nickname)
                            .putExtra("isAdmin",isAdmin).putExtra("roomID",roomID);
                    startActivity(intent);
                    finish();

                }else{

                    isSearchExecuted=true;
                    Intent intent = new Intent(getApplicationContext(), VoteActivity.class).putExtra("nickname",nickname).
                            putExtra("roomID",roomID);
                    startActivity(intent);
                    finish();

                }
            }
        });


        kd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                fReference =  FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID).child("Players").child(nickname);


                Map<String, Object> playerInfo = new HashMap<>();
                playerInfo.put("songID", "DzfaCVQfYXU");
                playerInfo.put("songName", "SENTINO - Niebieski Ptak");

                fReference.updateChildren(playerInfo);

                Toast.makeText(SearchActivity.this, nickname, Toast.LENGTH_SHORT).show();
                submit.setEnabled(false);

                selectedSong.setText("The Notorious B.I.G. - Juicy (Official Video) [4K]");
                if (isAdmin.equals("true")) {
                    Intent intent = new Intent(SearchActivity.this, AdminActivity.class).putExtra("nickname",nickname)
                            .putExtra("isAdmin",isAdmin).putExtra("roomID",roomID);
                    startActivity(intent);
                    finish();

                }else{

                    isSearchExecuted=true;
                    Intent intent = new Intent(getApplicationContext(), VoteActivity.class).putExtra("nickname",nickname).
                            putExtra("roomID",roomID);
                    startActivity(intent);

                }
            }
        });



    }




    private void Search(String query){




        String key2="AIzaSyC2Eg7NOATbUVFBjmlru8SrPm-Uw76dmo4";
        String key = "AIzaSyAmyFm-olEP5Ut3h5DoHMWQnbK06C7qSSk";
       String val = query;
        Call<VideoDetails> videoModelCall = NetworkInstance.getInstance().getAPI().getVideoData("snippet",val,key2,"relevance",1);

        isSearchExecuted = false;
        videoModelCall.enqueue(new Callback<VideoDetails>() {
            @Override
            public void onResponse( Call<VideoDetails> call, Response<VideoDetails> response) {

                assert response.body() != null;

                uiHandler.post(() -> setRecycleView(response.body().getItems()));
                dismissDialog();





            }

            @Override
            public void onFailure(@NonNull Call<VideoDetails> call, Throwable t) {
                //isSearchExecuted = false;
                dismissDialog();

            }
        });
    }



    private void showDialog() {
        progressDialog.show();
    }
    private void dismissDialog() {
        progressDialog.dismiss();
    }


    private void setRecycleView(Item[] items) {


        VideoDetailsAdapter myAdapter = new VideoDetailsAdapter(SearchActivity.this,items);

        myAdapter.setOnItemClickListener(new VideoDetailsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String id = items[position].getId().getVideoId();
                String title = items[position].getSnippet().getTitle();
              //  Toast.makeText(SearchActivity.this, id, Toast.LENGTH_SHORT).show();

                submit.setVisibility(View.VISIBLE);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fReference = FirebaseDatabase.getInstance("https://czyjatomelodia-f4d18-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference("Rooms").child(roomID).child("Players").child(nickname);


                      // Map<String, Object> songInfo = new HashMap<>();
                      //  songInfo.put("songID", id);

                        Log.i(TAG, id);
                        Log.i(TAG, roomID);
                        Log.i(TAG, nickname);

                        Map<String, Object> playerInfo = new HashMap<>();
                        playerInfo.put("songID", id);
                        playerInfo.put("songName", title);

                        fReference.updateChildren(playerInfo);

                        Toast.makeText(SearchActivity.this, nickname, Toast.LENGTH_SHORT).show();
                        submit.setEnabled(false);
                        myAdapter.setSelected(true);
                        selectedSong.setText(title);

                        if (isAdmin.equals("true")) {
                            Intent intent = new Intent(SearchActivity.this, AdminActivity.class).putExtra("nickname",nickname)
                                    .putExtra("isAdmin",isAdmin).putExtra("roomID",roomID);
                            startActivity(intent);
                            finish();

                        }else{
                            Intent intent = new Intent(SearchActivity.this, VoteActivity.class).putExtra("nickname",nickname)
                                    .putExtra("isAdmin",isAdmin).putExtra("roomID",roomID);
                            startActivity(intent);
                            finish();
                        }

                    }
                });


            }
        });

        RecyclerView.LayoutManager  lm = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(myAdapter);
       recyclerView.setVisibility(View.VISIBLE);



    }



}







