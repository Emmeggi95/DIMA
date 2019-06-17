package com.dima.emmeggi95.jaycaves.me.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NotificationOpenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_notification_open);

        String type = getIntent().getStringExtra("type");
    }
}
