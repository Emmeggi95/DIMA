package com.dima.emmeggi95.jaycaves.me.entities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.activities.MainActivity;
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
        String type = remoteMessage.getData().get("type");


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.music_explorer)
                .setAutoCancel(true)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        int notification_id = (int) System.currentTimeMillis();

       if (!type.equals("like")) {
           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
           int badgeCount = preferences.getInt("badge_count", 0);
           SharedPreferences.Editor editor = preferences.edit();
           editor.putInt("badge_count", badgeCount+1);
           editor.apply();
           ShortcutBadger.applyCount(getApplicationContext(), badgeCount+1);
       }

        Intent resultIntent = new Intent( this, MainActivity.class);
        resultIntent.putExtra("type", type);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(notification_id, builder.build());



    }

}
