package com.usoroos.usorosyncprototype;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Objects;


public class NotificationService extends NotificationListenerService {

    private static final String TAG = "NotificationListener";
    private Context context;
    private String icon;
    static boolean mListenerRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service running");
        context = getApplicationContext();
        mListenerRunning =true;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text = Objects.requireNonNull(extras.getCharSequence("android.text")).toString();
        Bitmap icon = (Bitmap) extras.get(Notification.EXTRA_LARGE_ICON);

        Log.i("Package",pack);
        Log.i("Text",text);

        Intent msgrcv = new Intent("notification");
        msgrcv.putExtra("package", pack);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);
        if(icon != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            icon.compress(Bitmap.CompressFormat.PNG, 95, stream);
            byte[] byteArray = stream.toByteArray();
            msgrcv.putExtra("icon",byteArray);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"Notification Removed");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        mListenerRunning = false;
    }

}
