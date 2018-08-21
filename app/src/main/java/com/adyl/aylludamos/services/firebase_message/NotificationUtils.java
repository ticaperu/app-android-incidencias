package com.adyl.aylludamos.services.firebase_message;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.adyl.aylludamos.R;


public class NotificationUtils {

    private final Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    void showNotificationMessage1(String title, String message, String timeStamp, Intent intent) {
        showNotificationMessage(title, message, timeStamp, intent);
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;
        // notification icon
        final int icon = R.mipmap.ic_launcher;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent);
    }


    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent) {

        NotificationCompat.BigTextStyle notiStyle =new NotificationCompat.BigTextStyle();
        notiStyle.bigText(message);
        Uri alarmSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] pattern = {1000, 1000};
        Bitmap ic= BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo);


        @SuppressLint("ResourceAsColor")
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_principal)
                        .setContentTitle(title)
                        .setSound(alarmSound)
                        .setLargeIcon(ic)
                        .setContentText(message)
                        .setLights(0xff493C7C, 1000, 1000)
                        .setVibrate(pattern)
                        .setColor(R.color.colorAccent)
                        .setStyle(notiStyle)
                        .setBadgeIconType(R.drawable.ic_principal)
                        .setSmallIcon(R.drawable.ic_principal)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setGroupSummary(true)
                        .setContentIntent(resultPendingIntent);
        showNotificationMessageAcademia(builder);
    }


    private long getTimeMilliSec(String timeStamp) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public  void  showNotificationMessageAcademia(NotificationCompat.Builder builder ){

        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wl.acquire(15000);
    }

}