package com.ahmedmatem.android.chesstar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedmatem.android.chesstar.config.Constants;
import com.ahmedmatem.android.chesstar.http.RetrofitBuilder;
import com.ahmedmatem.android.chesstar.models.Player;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mMessageReceiver;

    private Preferences mPreference;

    private TextView mWelcomeMessage;
    private Button mPlayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreference = new Preferences(this);

        mWelcomeMessage = findViewById(R.id.welcome_message);
        mWelcomeMessage.setText(String.format(getString(R.string.welcome_message),
                mPreference.getName()));
        mPlayButton = (Button) findViewById(R.id.btn_play);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Player> call =
                        RetrofitBuilder.httpService.findOpponent(mPreference.getName(),
                                mPreference.getToken());
                call.enqueue(new Callback<Player>() {
                    @Override
                    public void onResponse(Call<Player> call, Response<Player> response) {
                        Player opponent = response.body();
                        if(opponent != null) {
                            Toast.makeText(MainActivity.this, opponent.toString(),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Waiting for opponent",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Player> call, Throwable t) {
                    }
                });
            }
        });

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");
                Log.d(TAG, "Got message: " + message);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter(Constants.XXX_ACTION));
        super.onResume();
    }
}
