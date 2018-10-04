package com.ahmedmatem.android.chesstar.http;

import com.ahmedmatem.android.chesstar.models.Player;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ChesstarHttpService {
    @GET("onPlay")
    Call<Player> findOpponent(@Query("name") String name, @Query("token") String token);
}
