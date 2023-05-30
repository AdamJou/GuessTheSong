package com.example.czyjatomelodia;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FirebaseManager {
    private static FirebaseManager instance;
    private DatabaseReference databaseReference;
    private DatabaseReference playersRef;
    private ChildEventListener playersListener;


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
    public void deleteRoundNodes(String roomId) {
        DatabaseReference roundsRef = databaseReference.child("Rooms").child(roomId);
        roundsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String nodeName = childSnapshot.getKey();
                    if (nodeName.startsWith("Round")) {
                        childSnapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Obsługa błędów
            }
        });
    }

    public void setNumberOfRounds(String roomID) {
        DatabaseReference roomRef = databaseReference.child("Rooms").child(roomID);
        roomRef.child("NumberOfRounds").setValue(0);
    }
    public void setCurrentSong(String roomID) {
        DatabaseReference roomRef = databaseReference.child("Rooms").child(roomID);
        roomRef.child("Current").setValue("BRAK");
    }

    public void getNumberOfRounds(String roomID, final OnNumberOfRoundsCallback callback) {
        DatabaseReference numberOfRoundsRef = databaseReference.child("Rooms").child(roomID).child("NumberOfRounds");
        numberOfRoundsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int numberOfRounds = dataSnapshot.getValue(Integer.class);
                    callback.onSuccess(numberOfRounds);
                } else {
                    callback.onError("Number of rounds not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    public interface OnNumberOfRoundsCallback {
        void onSuccess(int numberOfRounds);
        void onError(String errorMessage);
    }



    public void areAllPlayersSelected(String roomID, final OnAllPlayersSelectedCallback callback) {
        DatabaseReference playersRef = databaseReference.child("Rooms").child(roomID).child("Players");
        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean allPlayersSelected = true;
                for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                    String isAdmin = playerSnapshot.child("isAdmin").getValue(String.class);
                    String isSelected = playerSnapshot.child("selected").getValue(String.class);

                    if (isAdmin.equals("false") && isSelected.equals("false")) {
                        allPlayersSelected = false;
                        callback.onPlayerNotSelected(false);
                        break;
                    }
                }
                callback.onSuccess(allPlayersSelected);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    public interface OnAllPlayersSelectedCallback {
        void onSuccess(boolean allPlayersSelected);
        void onError(String errorMessage);
        void onPlayerNotSelected(boolean notSelected);
    }


    public void setPlayingStatusInRoom(String roomID, String status) {
        DatabaseReference roomRef = databaseReference.child("Rooms").child(roomID);
        roomRef.child("Status").setValue(status);
    }


    public void setUnselectedForNonAdminPlayers(String roomID) {
        DatabaseReference playersRef = databaseReference.child("Rooms").child(roomID).child("Players");
        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                   /* String isAdmin = playerSnapshot.child("isAdmin").getValue(String.class);
                    if (isAdmin.equals("false")) {
                        playerSnapshot.getRef().child("selected").setValue("false");
                    }*/
                    playerSnapshot.getRef().child("selected").setValue("false");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }






    public void setReady(String roomID,String name) {
        DatabaseReference selectedRef = databaseReference.child("Rooms").child(roomID).child("Players").child(name).child("songID");
        selectedRef.setValue("ready");
    }

    public void startListeningForPlayerChanges(String roomId, final OnPlayersReadyCallback callback) {
        playersRef = databaseReference.child("Rooms").child(roomId).child("Players");

        playersListener = playersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                checkIfAllPlayersReady(callback);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                checkIfAllPlayersReady(callback);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                checkIfAllPlayersReady(callback);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Nie jest potrzebne w tym przypadku
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Obsługa błędów
            }
        });
    }



    public void getCurrentAdminNickname(String roomId, final OnNicknameCallback callback) {
        DatabaseReference playersRef = databaseReference.child("Rooms").child(roomId).child("Players");
        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                    String isAdmin = playerSnapshot.child("isAdmin").getValue(String.class);
                    if (isAdmin != null && isAdmin.equals("true")) {
                        String nickname = playerSnapshot.getKey();
                        if (nickname != null) {
                            callback.onSuccess(nickname);
                            return;
                        }
                    }
                }
                callback.onError("Admin nickname not found");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    public void stopListeningForPlayerChanges() {
        if (playersRef != null && playersListener != null) {
            playersRef.removeEventListener(playersListener);
        }
    }

    public void assignDJ(String roomId, final OnDJAssignedCallback callback) {
        DatabaseReference playersRef = databaseReference.child("Rooms").child(roomId).child("Players");
        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> eligiblePlayers = new ArrayList<>();
                List<String> previousDJs = new ArrayList<>();
                int maxScore = 0;

                // Znalezienie graczy uprawnionych do zostania DJ-em oraz gracza, który był DJ-em w poprzedniej rundzie
                for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                    String isAdmin = playerSnapshot.child("isAdmin").getValue(String.class);
                    int score = playerSnapshot.child("Score").getValue(Integer.class);

                    if ("false".equals(isAdmin)) {
                        eligiblePlayers.add(playerSnapshot.getKey());
                    }

                    if ("true".equals(isAdmin) || "was".equals(isAdmin)) {
                        previousDJs.add(playerSnapshot.getKey());
                    }

                    if (score > maxScore) {
                        maxScore = score;
                    }
                }

                // Wybór nowego DJ-a na podstawie wyników graczy
                String newDJId;
                if (!eligiblePlayers.isEmpty()) {
                    List<String> highestScoringPlayers = new ArrayList<>();
                    for (String playerId : eligiblePlayers) {
                        int score = dataSnapshot.child(playerId).child("Score").getValue(Integer.class);
                        if (score == maxScore) {
                            highestScoringPlayers.add(playerId);
                        }
                    }
                    newDJId = highestScoringPlayers.get(new Random().nextInt(highestScoringPlayers.size()));
                } else {
                    // W przypadku braku graczy uprawnionych do zostania DJ-em, ustawiamy wszystkim graczom isAdmin na "was"
                    updateIsAdminFields(playersRef, previousDJs);
                    callback.onDJAssigned(null); // Informujemy, że nie ma nowego DJ-a
                    return;
                }

                // Aktualizacja pól isAdmin
                updateIsAdminFields(playersRef, newDJId, previousDJs);

                callback.onDJAssigned(newDJId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Obsługa błędów
            }
        });
    }

    private void updateIsAdminFields(DatabaseReference playersRef, String newDJId, List<String> previousDJs) {
        for (String playerId : previousDJs) {
            if (!playerId.equals(newDJId)) {
                playersRef.child(playerId).child("isAdmin").setValue("was");
            }
        }

        playersRef.child(newDJId).child("isAdmin").setValue("true");
    }

    private void updateIsAdminFields(DatabaseReference playersRef, List<String> previousDJs) {
        for (String playerId : previousDJs) {
            playersRef.child(playerId).child("isAdmin").setValue("was");
        }
    }

    public interface OnDJAssignedCallback {
        void onDJAssigned(String djId);
    }



    public void checkIfCurrentUserIsAdmin(String roomID, final OnIsAdminCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            DatabaseReference userRef= databaseReference.child("Users").child(uid).child("Nickname");

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    String nickname = snapshot.getValue(String.class);

                    DatabaseReference currentUserRef = databaseReference.child("Rooms").child(roomID).child("Players").child(nickname);
                    currentUserRef.child("isAdmin").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String isAdmin = dataSnapshot.getValue(String.class);
                            if (isAdmin != null && isAdmin.equals("true")) {
                                callback.onIsAdmin("true");
                            } else {
                                callback.onIsAdmin("false");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Obsługa błędów
                        }
                    });
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                }
            });


        } else {
            callback.onIsAdmin("xd");
        }
    }

    public interface OnIsAdminCallback {
        void onIsAdmin(String isAdmin);
    }



    public void checkIfAllPlayersWereDJs(String roomId, final OnAllPlayersDJsCallback callback) {
        DatabaseReference roomRef = databaseReference.child("Rooms").child(roomId).child("Players");

        roomRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean allPlayersWereDJs = true;

                for (DataSnapshot playerSnapshot : task.getResult().getChildren()) {
                    String isAdmin = playerSnapshot.child("isAdmin").getValue(String.class);
                    if (!"was".equals(isAdmin)) {
                        allPlayersWereDJs = false;
                        break;
                    }
                }

                if (allPlayersWereDJs) {
                    callback.onAllPlayersDJs();
                }
            } else {
                // Obsługa błędów
            }
        });
    }

    public interface OnAllPlayersDJsCallback {
        void onAllPlayersDJs();
    }


    private void checkIfAllPlayersReady(final OnPlayersReadyCallback callback) {
        playersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean allPlayersReady = true;

                for (DataSnapshot playerSnapshot : task.getResult().getChildren()) {
                    String selected = playerSnapshot.child("songID").getValue(String.class);
                    if (!"ready".equals(selected)) {
                        allPlayersReady = false;
                        break;
                    }
                }

                if (allPlayersReady) {
                    callback.onPlayersReady();
                }
            } else {
                // Obsługa błędów
            }
        });
    }

    public interface OnPlayersReadyCallback {
        void onPlayersReady();
    }

    public void clearSongDataForAllPlayers(String roomId) {
        DatabaseReference playersRef = databaseReference.child("Rooms").child(roomId).child("Players");
        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference playerRef = playerSnapshot.getRef();
                    playerRef.child("songID").setValue("null");
                    playerRef.child("songName").setValue("null");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Obsługa błędów
            }
        });
    }

    public void getNumberOfPlayersInRoom(String roomID, final OnNumberOfPlayersCallback callback) {
        DatabaseReference playersRef = databaseReference.child("Rooms").child(roomID).child("Players");
        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int numberOfPlayers = (int) dataSnapshot.getChildrenCount();
                    callback.onSuccess(numberOfPlayers);
                } else {
                    callback.onError("Players not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    public interface OnNumberOfPlayersCallback {
        void onSuccess(int numberOfPlayers);
        void onError(String errorMessage);
    }






    public void incrementNumberOfRounds(String roomID) {
        DatabaseReference roundsRef = FirebaseManager.getInstance().getDatabaseReference().child("Rooms").child(roomID).child("NumberOfRounds");
        roundsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int currentNumberOfRounds = snapshot.getValue(Integer.class);
                    int newNumberOfRounds = currentNumberOfRounds + 1;
                    roundsRef.setValue(newNumberOfRounds);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Obsługa błędów
            }
        });
    }






    public interface OnNicknameCallback {
        void onSuccess(String nickname);
        void onError(String errorMessage);
    }




}
