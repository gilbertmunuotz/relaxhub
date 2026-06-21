package com.relaxhub.frontend.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.relaxhub.frontend.R;
import com.relaxhub.frontend.ui.dashboard.DashboardActivity;

public final class NotificationHelper {

    public static final String CHANNEL_GENERAL = "relaxhub_general";

    private static final String PREFS = "relaxhub_notifications";
    private static final String KEY_WELCOME_SHOWN = "welcome_shown";

    private NotificationHelper() {
    }

    public static void createChannels(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_GENERAL,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription(context.getString(R.string.notification_channel_description));
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    public static void showWelcomeIfNeeded(Context context, String fullName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (prefs.getBoolean(KEY_WELCOME_SHOWN, false)) {
            return;
        }

        String title = context.getString(R.string.notification_welcome_title);
        String body = fullName == null || fullName.isEmpty()
                ? context.getString(R.string.notification_welcome_body_generic)
                : context.getString(R.string.notification_welcome_body, fullName);

        show(context, 1001, title, body);
        prefs.edit().putBoolean(KEY_WELCOME_SHOWN, true).apply();
    }

    public static void showReceiptSaved(Context context, String placeName) {
        String title = context.getString(R.string.notification_receipt_title);
        String body = context.getString(R.string.notification_receipt_body, placeName);
        show(context, (int) System.currentTimeMillis(), title, body);
    }

    private static void show(Context context, int id, String title, String body) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_GENERAL)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat.from(context).notify(id, builder.build());
    }
}
