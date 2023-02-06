package com.example.czyjatomelodia;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {


    DatabaseReference fReference;
    FirebaseDatabase fDatabase;
    FirebaseAuth fAuth;

    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fAuth = FirebaseAuth.getInstance();
        username= findViewById(R.id.usernameTxt);





        FirebaseUser user = fAuth.getCurrentUser();
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


            fReference=FirebaseDatabase.getInstance("https://czyjatomelodia-f4d18-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
            fReference.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){

                        DataSnapshot snapshot = task.getResult();
                        String nick = String.valueOf(snapshot.child("Nickname").getValue());
                        Toast.makeText(HomeActivity.this,nick,Toast.LENGTH_SHORT).show();
                        username.setText(nick);

                    }else{
                        Toast.makeText(HomeActivity.this,"chujnia",Toast.LENGTH_SHORT).show();
                    }
                }
            });




    }




    }
