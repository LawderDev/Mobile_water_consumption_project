package com.example.water_consumption_project.Services;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.ALARM_SERVICE;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.water_consumption_project.Controllers.ReminderController;
import com.example.water_consumption_project.DataBase.DBManagement;
import com.example.water_consumption_project.MainActivity;
import com.example.water_consumption_project.Models.Reminder;
import com.example.water_consumption_project.Models.User;
import com.example.water_consumption_project.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NotificationAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isInitAlarm = Objects.requireNonNull(intent.getExtras()).getBoolean("isInitAlarm");

        if(isInitAlarm){
            setRemindersAlarms(context);
            return;
        }

        String channelId = "channel01";
        Log.d("ALARMNOTIF", "GOODcreate");
        NotificationManager notificationManager = createChannel(channelId, context);
        Log.d("ALARMNOTIF", "GOODSTART");

        if(notificationManager != null) sendNotification(notificationManager, channelId, context);
        Log.d("ALARMNOTIF", "GOODEND");
    }

    private static long getTomorrowTime(){
        Calendar calendar = Calendar.getInstance();

        // Ajoutez un jour à la date actuelle pour obtenir la date de demain
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        // Réglez l'heure à 00h01
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static void setRemindersAlarms(Context context)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        setInitAlarm(context, alarmManager);
        DBManagement dbManagement = DBManagement.getInstance(context);
        User user = dbManagement.getUser();
        ReminderController reminderController = dbManagement.getReminderController();
        reminderController.open();
        List<Reminder> reminders = reminderController.getRemindersByIdUser(user.getId());
        reminderController.close();

        int cptId = 1;
        for (Reminder reminder : reminders) {
            // Intent pour lancer le BroadcastReceiver
            Intent intentAlarm = new Intent(context, NotificationAlarmReceiver.class);
            intentAlarm.putExtra("isInitAlarm", false);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, cptId, intentAlarm, PendingIntent.FLAG_IMMUTABLE);

            long triggerTime = convertHourToTimestamp(reminder.getHour());
            Log.d("enterheree", "herre");
            // Définissez l'alarme répétée
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, interval, pendingIntent);
            Log.d("enterheree", "herre2");
            cptId++;
        }
    }

    private static void setInitAlarm(Context context, AlarmManager alarmManager){
        Intent intentAlarm = new Intent(context, NotificationAlarmReceiver.class);
        intentAlarm.putExtra("isInitAlarm", true);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentAlarm, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getTomorrowTime(), pendingIntent);
    }

    private static long convertHourToTimestamp(String inputTime) {
        String[] parts = inputTime.split("h");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        // Créer une instance de Calendar et définir les heures et les minutes
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK) - 1);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);

        // Obtenir le timestamp à partir de l'objet Calendar
        return calendar.getTimeInMillis();
    }

    private void sendNotification(NotificationManager notificationManager, String channelId, Context context){
        Log.d("ALARMNOTIF", "GOOD");
        int notificationId = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(new Date()));
        notificationManager.notify(notificationId, createBuilder(channelId, context).build());
    }

    private NotificationManager createChannel(String channelId, Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "test";
            String description = "test";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
            return notificationManager;
        }
        return null;
    }

    private NotificationCompat.Builder createBuilder(String channelId, Context context){
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.menu)
                .setContentTitle("Don't forget to drink water !")
                .setContentText("Keep your good habbits ! ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

}
