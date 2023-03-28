package com.example.czyjatomelodia;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.czyjatomelodia.Adapter.VideoDetailsAdapter;
import com.example.czyjatomelodia.Model.Item;
import com.example.czyjatomelodia.Model.VideoDetails;
import com.example.czyjatomelodia.Network.NetworkInstance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

        TextView username;
        Button btn;
        EditText txt;
        FirebaseAuth fAuth;
        VideoDetailsAdapter videoDetailsAdapter ;
      RecyclerView recyclerView;
    private final String TAG = SearchActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        btn = (Button)findViewById(R.id.btnYt);
        txt=(EditText) findViewById(R.id.ytTxt);
        fAuth = FirebaseAuth.getInstance();
       // username=findViewById(R.id.userUID);
        recyclerView = (RecyclerView)findViewById(R.id.ytRecycle);


        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        recyclerView.hasFixedSize();





        FirebaseUser user = fAuth.getCurrentUser();
      //  username.setText(user.getUid());


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search();
            }
        });








    }

    private void Search(){

        String key = "AIzaSyAmyFm-olEP5Ut3h5DoHMWQnbK06C7qSSk";
       String val = txt.getText().toString();
        Call<VideoDetails> videoModelCall = NetworkInstance.getInstance().getAPI().getVideoData("snippet",val,"<API_KEY>","relevance", "10");


        videoModelCall.enqueue(new Callback<VideoDetails>() {
            @Override
            public void onResponse( Call<VideoDetails> call, Response<VideoDetails> response) {
                //  assert response.body() != null;
                assert response.body() != null;
                setRecycleView(response.body().getItems());
            }

            @Override
            public void onFailure(@NonNull Call<VideoDetails> call, Throwable t) {

            }
        });
    }





    private void setRecycleView(Item[] items) {
        VideoDetailsAdapter myAdapter = new VideoDetailsAdapter(SearchActivity.this,items);


        RecyclerView.LayoutManager  lm = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(myAdapter);
       recyclerView.setVisibility(View.VISIBLE);


    }


/*
    public void Search(){

        GetDatService datService = NetworkInstance.getRetrofit().create(GetDatService.class);


        Call<VideoDetails> videoDetailsRequest = datService.getVideoData("snippet",val,"AIzaSyAmyFm-olEP5Ut3h5DoHMWQnbK06C7qSSk","relevance","10");

        videoDetailsRequest.enqueue(new Callback<VideoDetails>() {
            @Override
            public void onResponse(Call<VideoDetails> call, Response<VideoDetails> response) {
                if(response.isSuccessful()){
                    if(response.body()!=null){
                        Log.e(TAG, "Response Succes");

                        //  Toast.makeText(MainActivity.this, "LADOWANIE", Toast.LENGTH_LONG).show();

                       //items = response.body().getItems();

                        setUpRecyclerView(response.body().getItems());


                    }
                }

            }



            @Override
            public void onFailure(Call<VideoDetails> call, Throwable t) {

                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG.concat(" API REQUEST FAILED "), t.getMessage());
            }
        });


    }*/
/*

   public void setUpRecyclerView(List<Item> items) {

        videoDetailsAdapter = new VideoDetailsAdapter(MainActivity.this,items);
        RecyclerView.LayoutManager  lm = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(videoDetailsAdapter);


    }

*/



}







