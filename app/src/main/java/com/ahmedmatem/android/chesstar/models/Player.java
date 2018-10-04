package com.ahmedmatem.android.chesstar.models;

import com.ahmedmatem.android.chesstar.contracts.State;

public class Player {
    public String name;
    public String token;
    public String state = State.Default.toString();

    public Player(String name, String token) {
        this.name = name;
        this.token = token;
    }

    public Player(String name, String token, String state) {
        this(name, token);
        this.state = state;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", token='" + token + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
