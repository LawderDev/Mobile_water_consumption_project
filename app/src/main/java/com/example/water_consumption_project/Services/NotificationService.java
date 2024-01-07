package com.example.water_consumption_project.Services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.water_consumption_project.Controllers.ReminderController;
import com.example.water_consumption_project.DataBase.DBManagement;
import com.example.water_consumption_project.MainActivity;
import com.example.water_consumption_project.Models.Reminder;
import com.example.water_consumption_project.Models.User;
import com.example.water_consumption_project.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationService extends Service {

    private NotificationManagerCompat notificationManager;
    NotificationCompat.Builder builder;
    private List<String> hours;
    private List<String> dayHours;
    private final Handler handler = new Handler();
    private static boolean runningService = false;

    public static boolean getRunningService() {
        return runningService;
    }

    private final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if(!runningService) return;
            for (String hour : hours) {
                if (isCurrentTimeToNotify(hour) && !dayHours.contains(hour)) {
                    sendNotification();
                    dayHours.add(hour);
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

    private boolean isCurrentTimeToNotify(String targetHour) {
        // Comparer l'heure actuelle avec l'heure programmée pour la notification
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        // Formatage de l'heure
        String formattedTime = String.format(Locale.getDefault(), "%dh%02d", currentHour, currentMinute);
        return formattedTime.equals(targetHour);
    }

    private void sendNotification(){
        int notificationId = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(new Date()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(notificationId,builder.build());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String channelId = "channel01";
        hours = new ArrayList<>();
        dayHours = new ArrayList<>();


        getRemindersHours();
        createChannel(channelId);
        createBuilder(channelId);

        notificationManager = NotificationManagerCompat.from(this);

        runningService = true;
        handler.postDelayed(runnable, 1000);
        return flags;
    }

    private void createChannel(String channelId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "test";
            String description = "test";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createBuilder(String channelId){
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE);

        builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.menu)
                .setContentTitle("Don't forget to drink water !")
                .setContentText("Keep your good habbits ! ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    private void getRemindersHours(){
        DBManagement dbManagement = DBManagement.getInstance(getApplicationContext());
        ReminderController reminderController = dbManagement.getReminderController();
        User user = dbManagement.getUser();

        reminderController.open();
        List<Reminder> reminders = reminderController.getRemindersByIdUser(user.getId());
        reminderController.close();

        for (Reminder reminder : reminders) {
            hours.add(reminder.getHour());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        runningService = false;
        builder = null;
        hours = null;
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
