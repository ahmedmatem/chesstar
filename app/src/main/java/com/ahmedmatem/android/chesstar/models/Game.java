package com.ahmedmatem.android.chesstar.models;

public class Game {
    public Player player1;
    public Player player2;
    public String state;

    public Game(Player player1, Player player2, String state) {
        this.player1 = player1;
        this.player2 = player2;
        this.state = state;
    }
}
