package com.ahmedmatem.android.chesstar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedmatem.android.chesstar.config.Constants;
import com.ahmedmatem.android.chesstar.http.RetrofitBuilder;
import com.ahmedmatem.android.chesstar.models.Player;
import com.ahmedmatem.chess.util.Alliance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ahmedmatem.android.chesstar.config.Constants.RESPONSE_CONTENT_PLAYER;
import static com.ahmedmatem.android.chesstar.config.Constants.OPPONENT_EXTRA;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mPlayerMessageReceiver;

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
        mPlayButton = findViewById(R.id.btn_play);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Player> call = RetrofitBuilder.httpService.findOpponent(
                        mPreference.getName(),
                        mPreference.getToken(),
                        RESPONSE_CONTENT_PLAYER);
                call.enqueue(new Callback<Player>() {

                    @Override
                    public void onResponse(Call<Player> call, Response<Player> response) {
                        Player opponent = response.body();
                        if(opponent != null) {
                            opponent.alliance = Alliance.BLACK;
                            Intent intent = new Intent(MainActivity.this,
                                    GameBoardActivity.class);
                            intent.putExtra(OPPONENT_EXTRA, opponent);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<Player> call, Throwable t) {
                        //no opponent found
                        Toast.makeText(MainActivity.this, "Waiting for opponent",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mPlayerMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(OPPONENT_EXTRA)) {
                    Player opponent = intent.getParcelableExtra(OPPONENT_EXTRA);
                    Intent gameBoardIntent =
                            new Intent(MainActivity.this, GameBoardActivity.class);
                    gameBoardIntent.putExtra(OPPONENT_EXTRA, opponent);
                    startActivity(gameBoardIntent);
                }
            }
        };
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPlayerMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mPlayerMessageReceiver, new IntentFilter(Constants.PLAYER_ACTION));
        super.onResume();
    }
}
