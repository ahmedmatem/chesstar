package com.ahmedmatem.android.chesstar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ahmedmatem.android.chesstar.asyncs.FirebaseInstanceIdAsync;
import com.ahmedmatem.android.chesstar.contracts.State;
import com.ahmedmatem.android.chesstar.models.Player;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class WelcomeActivity extends AppCompatActivity
        implements FirebaseInstanceIdAsync.FirebaseTokenListener {

    private FirebaseDatabase mDatabase;
    private Preferences mPref;

    private EditText mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mPref = new Preferences(getApplicationContext());
        // check if 'name' already created
        if(!mPref.getName().equals(
                getString(R.string.name_preference_default_key))){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        mDatabase = FirebaseDatabase.getInstance();

        mName = findViewById(R.id.et_name);
        Button nextButton = findViewById(R.id.btn_welcome_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FirebaseInstanceIdAsync(WelcomeActivity.this).execute();
            }
        });
    }

    @Override
    public void onTokenReceived(String token) {
        String name = mName.getText().toString();
        if (name.isEmpty()) {
            name = getString(R.string.name_preference_default_key);
        }
        mPref.setName(name);
        mPref.setToken(token);

        Player me = new Player(name, token, State.Default.toString());
        upload(me);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void upload(Player player) {
        DatabaseReference ref = mDatabase.getReference();
        ref.child("players").child(player.token).setValue(player);
    }
}
