package com.dima.emmeggi95.jaycaves.me.entities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.activities.NotificationOpenActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MyFirebaseMessagingService extends FirebaseMessagingService {



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String messageBody = remoteMessage.getData().get("body");
        String messageTitle = remoteMessage.getData().get("title");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.music_explorer)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        int notification_id = (int) System.currentTimeMillis();
        int badgeCount = Integer.parseInt(remoteMessage.getData().get("badge"));
        ShortcutBadger.applyCount(getApplicationContext(), badgeCount);


   /*   Intent resultIntent = new Intent(); // click_action
        resultIntent.putExtra("type", type);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent); */

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(notification_id, builder.build());



    }

}
