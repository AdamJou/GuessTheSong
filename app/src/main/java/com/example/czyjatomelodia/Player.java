package com.example.czyjatomelodia;

import android.graphics.drawable.Drawable;

public class Player {
    String name;
    String songID;
    boolean isAdmin=false;
    boolean isCorrect=false;
    int backgroundColor = 0;
    int playerID;
    Drawable drawable;

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    private Player() {}

    public Player(String name, int playerID) {
        this.name = name;
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public Player(String name) {
        this.name = name;
    }

    public Player(String name, String songID) {
        this.name = name;
        this.songID = songID;
    }

    public String getSongID(){
        return songID;
    }

    public void setSongID(String songID){
        this.songID = songID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}