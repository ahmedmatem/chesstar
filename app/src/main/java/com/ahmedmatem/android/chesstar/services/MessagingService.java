package com.ahmedmatem.android.chesstar.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ahmedmatem.android.chesstar.config.Constants;
import com.ahmedmatem.android.chesstar.models.Player;
import com.ahmedmatem.chess.util.Alliance;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static com.ahmedmatem.android.chesstar.config.Constants.DESTINATION_EXTRA;
import static com.ahmedmatem.android.chesstar.config.Constants.OPPONENT_EXTRA;
import static com.ahmedmatem.android.chesstar.config.Constants.RESPONSE_CONTENT_MOVE;
import static com.ahmedmatem.android.chesstar.config.Constants.RESPONSE_CONTENT_PLAYER;
import static com.ahmedmatem.android.chesstar.config.Constants.SOURCE_EXTRA;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String,String> data = remoteMessage.getData();

            switch (data.get("responseContent")) {
                case RESPONSE_CONTENT_MOVE:
                    int source = Integer.valueOf(data.get("source"));
                    int destination = Integer.valueOf(data.get("destination"));
                    sendMove(source, destination);
                    break;
                case RESPONSE_CONTENT_PLAYER:
                    Player player =
                            new Player(data.get("name"), data.get("token"), Alliance.WHITE);
                    sendPlayer(player);
                    break;
            }

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendMove(int source, int destination) {
        Intent intent = new Intent(Constants.MOVE_ACTION);
        intent.putExtra(SOURCE_EXTRA, source);
        intent.putExtra(DESTINATION_EXTRA, destination);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendPlayer(Player player) {
        Intent intent = new Intent(Constants.PLAYER_ACTION);
        intent.putExtra(OPPONENT_EXTRA, player);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleNow() {
        
    }

    private void scheduleJob() {

    }
}
