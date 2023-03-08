package com.example.ma01_20200942;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class MyReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmIntentServiceIntent = new Intent(context, MainActivity.class);
        context.startService(alarmIntentServiceIntent);

        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class),PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setContentTitle("날씨")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("오늘의 날씨를 확인해보세요");

        builder.setContentIntent(notificationIntent);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(100, builder.build());

    }


}
