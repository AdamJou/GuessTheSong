package com.example.czyjatomelodia;

public class Player {
    String name;
    String songID;
    boolean isAdmin=false;
    int backgroundColor = 0;

    private Player() {}

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

}