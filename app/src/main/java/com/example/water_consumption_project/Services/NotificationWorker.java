package com.example.water_consumption_project.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.water_consumption_project.MainActivity;
import com.example.water_consumption_project.R;

import java.util.Calendar;
import java.util.Locale;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        sendNotification();
        return Result.success();
        /*try {
            DBManagement dbManagement = DBManagement.getInstance(getApplicationContext());
            ReminderController reminderController = dbManagement.getReminderController();
            User user = dbManagement.getUser();

            reminderController.open();
            List<Reminder> reminders = reminderController.getRemindersByIdUser(user.getId());
            reminderController.close();

            for (Reminder reminder : reminders) {
                if (isCurrentTimeToNotify(reminder.getHour())) {
                    sendNotification();
                }
            }
            return Result.success();
        } catch (Throwable throwable) {
            Log.e("NotificationWorker", "Error in doWork: " + throwable.getMessage());
            return Result.failure();
        }*/
    }

    private boolean isCurrentTimeToNotify(String targetHour) {
        // Comparer l'heure actuelle avec l'heure programmée pour la notification
        Log.d("GIGANOTIF", targetHour);

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        // Formatage de l'heure
        String formattedTime = String.format(Locale.getDefault(), "%dh%02d", currentHour, currentMinute);
        Log.d("GIGANOTIF", formattedTime);
        return formattedTime.equals(targetHour);
    }

    private void sendNotification() {
        Log.d("NOTIF", "enter");
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d("GIGANOTIF", "enter1");
        // Créer un canal de notification (pour Android 8 et supérieur)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Nom du canal", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        Log.d("GIGANOTIF", "enter2");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "channel_id")
                .setContentTitle("Il est l'heure de boire de l'eau")
                .setSmallIcon(R.drawable.menu)
                .setContentText("Restez hydraté pour rester en bonne santé!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Log.d("GIGANOTIF", "enter3");
        // Intent pour lancer l'activité principale lorsque la notification est cliquée

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        Log.d("GIGANOTIF", "enter4");
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Log.d("GIGANOTIF", "enter5");
        builder.setContentIntent(contentIntent);

        Log.d("GIGANOTIF", "enter5");
        // Afficher la notification
        Notification notification = builder.build();

        notificationManager.notify(1, notification);
        Log.d("GIGANOTIF", "enter6");
    }
}
