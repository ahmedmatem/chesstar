package com.ahmedmatem.android.chesstar.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.ahmedmatem.android.chesstar.contracts.State;
import com.ahmedmatem.chess.util.Alliance;

public class Player implements Parcelable {
    public String name;
    public String token;
    public String state = State.Default.toString();
    public Alliance alliance;

    public Player(String name, String token) {
        this.name = name;
        this.token = token;
    }

    public Player(String name, String token, String state) {
        this(name, token);
        this.state = state;
    }

    public Player(String name, String token, Alliance alliance) {
        this(name, token);
        this.alliance = alliance;
    }

    public Player(String name, String token, String state, Alliance alliance) {
        this(name, token, alliance);
        this.state = state;
    }

    protected Player(Parcel in) {
        name = in.readString();
        token = in.readString();
        state = in.readString();
        int allianceAsInteger = in.readInt();
        alliance = allianceAsInteger == 1 ? Alliance.WHITE : Alliance.BLACK;
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(token);
        dest.writeString(state);
        dest.writeInt(alliance == Alliance.WHITE ? 1 : 0);
    }


}
