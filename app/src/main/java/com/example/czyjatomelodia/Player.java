package com.example.czyjatomelodia;

public class Player {
    String name;

    private  Player() {};

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
