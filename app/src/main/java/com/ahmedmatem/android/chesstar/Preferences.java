package com.ahmedmatem.android.chesstar;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    public static final String NAME_PREF_KEY = "com.ahmedmatem.android.chesstar.NAME_PREF";
    public static final String TOKEN_PREF_KEY = "com.ahmedmatem.android.chesstar.TOKEN_PREF";

    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public Preferences(Context context) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),Context.MODE_PRIVATE);
    }

    public String getName() {
        String defaultValue = mContext.getResources()
                .getString(R.string.name_preference_default_key);
        return mSharedPreferences.getString(NAME_PREF_KEY, defaultValue);
    }

    public void setName(String namePreference) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(NAME_PREF_KEY, namePreference);
        editor.commit();
    }

    public String getToken() {
        return mSharedPreferences.getString(TOKEN_PREF_KEY, "");
    }

    public void setToken(String token) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(TOKEN_PREF_KEY, token);
        editor.commit();
    }
}