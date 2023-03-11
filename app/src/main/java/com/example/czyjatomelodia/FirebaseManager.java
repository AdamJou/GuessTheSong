package com.example.czyjatomelodia;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseManager {
    private static FirebaseManager instance;
    private DatabaseReference databaseReference;



    private FirebaseManager() {
        // Inicjalizacja bazy danych
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://czyjatomelodia-f4d18-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();

    }

    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }


    public void getNickname(final String path, final OnNicknameCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference nicknameRef = databaseReference.child(path).child(uid).child("Nickname");
            nicknameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String nickname = dataSnapshot.getValue(String.class);
                        callback.onSuccess(nickname);
                    } else {
                        callback.onError("Nickname not found");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onError(databaseError.getMessage());
                }
            });
        } else {
            callback.onError("User not logged in");
        }
    }

    public interface OnNicknameCallback {
        void onSuccess(String nickname);
        void onError(String errorMessage);
    }

}
