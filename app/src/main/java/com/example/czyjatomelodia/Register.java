package com.example.czyjatomelodia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.czyjatomelodia.Base.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends BaseActivity {

    DatabaseReference fReference;
    FirebaseDatabase fDatabase;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    EditText email, nickname, password;
    Button  registerSubmit;
    TextView registerToLogIn;
    boolean valid = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        email = findViewById(R.id.emailRegister);
        nickname = findViewById(R.id.usernameRegister);
        password = findViewById(R.id.passwordRegister);
        registerSubmit = findViewById(R.id.registerSubmit);
        registerToLogIn = findViewById(R.id.registerToLogIn);
        fDatabase = FirebaseDatabase.getInstance("https://czyjatomelodia-f4d18-default-rtdb.europe-west1.firebasedatabase.app/");


      registerSubmit.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {



              checkField(email);
              checkField(nickname);
              checkField(password);




              if(valid){

                  String em = email.getText().toString().trim();
                  String p = password.getText().toString().trim();
                  String nick=nickname.getText().toString().trim();


                  fAuth.createUserWithEmailAndPassword(em,p).addOnSuccessListener(authResult -> {



                      String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                      FirebaseUser user = fAuth.getCurrentUser();
                      fReference =  fDatabase.getReference().child("Users").child(uid);

                      Toast.makeText(Register.this,"Utworzono konto", Toast.LENGTH_SHORT).show();
                //      DocumentReference df = fStore.collection("Users").document(user.getUid());
                      Map<String,Object> userInfo = new HashMap<>();
                      userInfo.put("Nickname",nick);
                     userInfo.put("Email", em);
                    //  df.set(userInfo);
                       fReference.setValue(userInfo);

                      Intent intent = new Intent(Register.this, HomeActivity.class);
                      startActivity(intent);
                      finish();
                  }).addOnFailureListener(e -> Toast.makeText(Register.this,"Nie udało się utworzyć konta", Toast.LENGTH_LONG).show());


              }
          }
      });


        registerToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));

            }
        });




    }
    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("To pole nie może być puste");
            valid = false;
        }else{
            valid=true;
        }

        return valid;
    }





}