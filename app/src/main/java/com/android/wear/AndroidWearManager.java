package com.android.wear;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.wenxi.carmap.MainActivity;
import com.example.wenxi.carmap.R;

/**
 * Created by wenxi on 2017/2/14.
 */

public class AndroidWearManager {


    private static AndroidWearManager androidWearManager=new AndroidWearManager();
    private NotificationManagerCompat mNotificationManagerCompat;
    private AndroidWearManager(){}

    public static AndroidWearManager getAndroidWearManager() {
        return androidWearManager;
    }

    public void upNotification(Intent viewIntent,PendingIntent mainPendingIntent,Context context,AndroidWearDatabase database){
        mNotificationManagerCompat = NotificationManagerCompat.from(context);
        NotificationCompat.BigPictureStyle bigPictureStyle=new NotificationCompat.BigPictureStyle();
        bigPictureStyle.bigPicture(BitmapFactory.decodeResource(context.getResources(),database.getImage()));
        bigPictureStyle.setSummaryText(database.getContentTitle());
        bigPictureStyle.setBigContentTitle(database.getContentText());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(viewIntent);
        android.support.v7.app.NotificationCompat.Action dismissAction =
                new android.support.v7.app.NotificationCompat.Action.Builder(
                        R.drawable.ic_reply_white_18dp,
                        database.getDismissAction(),
                        mainPendingIntent)
                        .build();
        android.support.v7.app.NotificationCompat.Builder notificationCompatBuilder =
                new android.support.v7.app.NotificationCompat.Builder(context);

        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(notificationCompatBuilder);
        Notification notification = notificationCompatBuilder
                // BIG_TEXT_STYLE sets title and content for API 16 (4.1 and after)
                .setStyle(bigPictureStyle)
                // Title for API <16 (4.0 and below) devices
                .setContentTitle(database.getContentTitle())
                // Content for API <24 (7.0 and below) devices
                .setContentText(database.getContentText())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        context.getResources(),
                        R.drawable.ic_reply_white_18dp))
                .setContentIntent(mainPendingIntent)
                // Set primary color (important for Wear 2.0 Notifications)
                .setColor(database.getColor())

                // SIDE NOTE: Auto-bundling is enabled for 4 or more notifications on API 24+ (N+)
                // devices and all Android Wear devices. If you have more than one notification and
                // you prefer a different summary notification, set a group key and create a
                // summary notification via
                // .setGroupSummary(true)
                // .setGroup(GROUP_KEY_YOUR_NAME_HERE)

                .setCategory(Notification.CATEGORY_REMINDER)
                .setPriority(Notification.PRIORITY_HIGH)

                // Shows content on the lock-screen
                .setVisibility(Notification.VISIBILITY_PUBLIC)

                // Adds additional actions specified above
                .addAction(dismissAction)

                .build();
        mNotificationManagerCompat.notify(1, notification);
    }

    public void close(Context context){
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(1);
    }
}
