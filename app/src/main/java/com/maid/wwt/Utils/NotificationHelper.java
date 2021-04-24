package com.maid.wwt.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.maid.wwt.R;

public class NotificationHelper extends ContextWrapper {
    private static final String WWT_CHANNEL_ID="package com.maid.wwt.Utils";
    private static final String WWT_CHANNEL_NAME="Realtime2020";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            createChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel wwtchannel =new NotificationChannel(WWT_CHANNEL_ID,WWT_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        wwtchannel.enableVibration(true);
        wwtchannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(wwtchannel);
    }

    public NotificationManager getManager() {
        if(manager==null)
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getRealtimeTrackingNotification(String title, String content, Uri defaultSound) {
        return new Notification.Builder(getApplicationContext(),WWT_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(defaultSound)
                .setAutoCancel(false);
    }
}