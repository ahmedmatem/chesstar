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

import static com.ahmedmatem.android.chesstar.config.Constants.OPPONENT_EXTRA;

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
            Player player = new Player(data.get("name"), data.get("token"), Alliance.BLACK);
            sendMessage(player);

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

    private void sendMessage(Player player) {
//        Log.d(TAG, "Broadcasting messaging");
        Intent intent = new Intent(Constants.NEW_GAME_ACTION);
        intent.putExtra(OPPONENT_EXTRA, player);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleNow() {
        
    }

    private void scheduleJob() {

    }
}
