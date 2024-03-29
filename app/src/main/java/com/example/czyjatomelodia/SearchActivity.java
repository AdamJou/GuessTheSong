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
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Adapter.VideoDetailsAdapter;
import com.example.czyjatomelodia.Base.BaseActivity;
import com.example.czyjatomelodia.Model.Item;
import com.example.czyjatomelodia.Model.VideoDetails;
import com.example.czyjatomelodia.Network.NetworkInstance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity {

    private final String TAG = "INFORMACJE";
    DatabaseReference fReference;
    TextView selectedSong;
    Button submit;
    FirebaseAuth fAuth;
    RecyclerView recyclerView;
    String nickname, isAdmin, roomID;
    SearchView search;
    private boolean isSearchExecuted = false;
    private ProgressDialog progressDialog;
    private Handler uiHandler;
    private boolean isRightFadeAnimationExecuted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        fAuth = FirebaseAuth.getInstance();


        selectedSong = findViewById(R.id.tvSelectedSong);
        recyclerView = findViewById(R.id.ytRecycle);
        submit = findViewById(R.id.btnSubmitSong);
        search = findViewById(R.id.ytTxt);

        progressDialog = new ProgressDialog(SearchActivity.this);
        progressDialog.setMessage("Ładowanie...");
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
        nickname = intent.getStringExtra("nickname");


        isAdmin = intent.getStringExtra("isAdmin");
        roomID = intent.getStringExtra("id");
        FirebaseManager.getInstance().clearSongDataForAllPlayers(roomID);



        Log.e(TAG, " CHECK IF ADMIN " + isAdmin);
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();



    }


    private void Search(String query) {


    
        String val = query;
        Call<VideoDetails> videoModelCall = NetworkInstance.getInstance().getAPI().getVideoData("snippet", val,  key, "relevance", 10);

        isSearchExecuted = false;
        videoModelCall.enqueue(new Callback<VideoDetails>() {
            @Override
            public void onResponse(Call<VideoDetails> call, Response<VideoDetails> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    setRecycleView(response.body().getItems());
                    dismissDialog();
                } else {
                    showServerErrorToast();
                    dismissDialog();
                }
            }

            @Override
            public void onFailure(@NonNull Call<VideoDetails> call, Throwable t) {
                //isSearchExecuted = false;
                dismissDialog();
                showServerErrorToast();


            }
        });
    }

    private void showServerErrorToast() {
        StyleableToast.makeText(getApplicationContext(), "Błąd serwera!", Toast.LENGTH_LONG, R.style.backToast).show();

    }


    private void showDialog() {
        progressDialog.show();
    }

    private void dismissDialog() {
        progressDialog.dismiss();
    }


    private void setRecycleView(Item[] items) {

        List<Item> filteredItems = new ArrayList<>();


        for (Item item : items) {
            if (item.getId() != null && item.getId().getVideoId() != null) {
                filteredItems.add(item);
            }
        }

        VideoDetailsAdapter myAdapter = new VideoDetailsAdapter(SearchActivity.this, items);

        myAdapter.setOnItemClickListener(new VideoDetailsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String id = items[position].getId().getVideoId();
                String title = items[position].getSnippet().getTitle();
                //  Toast.makeText(SearchActivity.this, id, Toast.LENGTH_SHORT).show();



                selectedSong.setText(title);
                if (!isRightFadeAnimationExecuted) {
                    submit.setVisibility(View.VISIBLE);
                    submit.startAnimation(rightfade);
                    isRightFadeAnimationExecuted = true;
                }


                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fReference = FirebaseDatabase.getInstance("https://czyjatomelodia-f4d18-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference("Rooms").child(roomID).child("Players").child(nickname);


                        Log.i(TAG, id);
                        Log.i(TAG, roomID);
                        Log.i(TAG, nickname);

                        Map<String, Object> playerInfo = new HashMap<>();
                        playerInfo.put("songID", id);
                        playerInfo.put("songName", regexTitle(title));

                        fReference.updateChildren(playerInfo);

                        Toast.makeText(SearchActivity.this, nickname, Toast.LENGTH_SHORT).show();
                        submit.setEnabled(false);
                        myAdapter.setSelected(true);
                        selectedSong.setText(title);

                        if (isAdmin.equals("true")) {
                            Intent intent = new Intent(SearchActivity.this, AdminActivity.class).putExtra("nickname", nickname)
                                    .putExtra("isAdmin", isAdmin).putExtra("roomID", roomID);
                            startActivity(intent);
                            finish();

                        } else {
                            Intent intent = new Intent(SearchActivity.this, VoteActivity.class).putExtra("nickname", nickname)
                                    .putExtra("isAdmin", isAdmin).putExtra("roomID", roomID);
                            startActivity(intent);
                            finish();
                        }

                    }
                });


            }
        });

        RecyclerView.LayoutManager lm = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setVisibility(View.VISIBLE);


    }

    private String regexTitle(String title) {

         String[] unwantedPhrases = {
                "Official Video",
                "Official Music Video",
                "Official Music Video",
                "Official",
                "Music",
                "Video",
                "Lyric Video",
                "HD",
                "HQ",
                "4K",
                "Remastered",
                "Audio",
                "Full Song",
                "Extended Version",
                "Original Version",
                "Acoustic",
                "Live Performance",
                "with Lyrics",
                "Official Lyric Video",
                "Official Video Clip",
                "[",
                "]",
                ")",
                "(",

        };

        for (String phrase : unwantedPhrases) {
            title = title.replace(phrase, "");
        }

        return title;
    }


}







