package com.ahmedmatem.android.chesstar.asyncs;

import android.os.AsyncTask;

import com.google.firebase.iid.FirebaseInstanceId;

public class FirebaseInstanceIdAsync extends AsyncTask<Void, Void, String> {
    private FirebaseTokenListener listener;

    public interface FirebaseTokenListener{
        public void onTokenReceived(String token);
    }

    public FirebaseInstanceIdAsync(FirebaseTokenListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    protected void onPostExecute(String token) {
        if (listener != null) {
            listener.onTokenReceived(token);
        }
    }
}
