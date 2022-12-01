package com.example.czyjatomelodia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

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


      registerSubmit.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {



              checkField(email);
              checkField(nickname);
              checkField(password);




              if(valid){

                  String em = email.getText().toString();
                  String p = password.getText().toString();

                  fAuth.createUserWithEmailAndPassword(em,p).addOnSuccessListener(authResult -> {

                      FirebaseUser user = fAuth.getCurrentUser();
                      Toast.makeText(Register.this,"Utworzono konto", Toast.LENGTH_SHORT).show();
                      DocumentReference df = fStore.collection("Users").document(user.getUid());
                      Map<String,Object> userInfo = new HashMap<>();
                      userInfo.put("Nickname", nickname.getText().toString());
                      userInfo.put("Email", email.getText().toString());
                      df.set(userInfo);



                      startActivity(new Intent(getApplicationContext(),MainActivity.class));
                      finish();
                  }).addOnFailureListener(e -> Toast.makeText(Register.this,"Nie udało się utworzyć konta", Toast.LENGTH_SHORT).show());


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