package com.ahmedmatem.android.chesstar.http;

import com.ahmedmatem.android.chesstar.config.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {
    static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static final ChesstarHttpService httpService =
            retrofit.create(ChesstarHttpService.class);
}
