package com.example.water_consumption_project.Services;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.AlarmManager;
import android.content.Context;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class NotificationWorker extends Worker {
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    @NonNull
    @Override
    public Result doWork() {
        String channelId = "channel01";
        Log.d("ALARMNOTIF", "GOODcreate");
        NotificationManager notificationManager = createChannel(channelId, getApplicationContext());
        Log.d("ALARMNOTIF", "GOODSTART");
        if(notificationManager != null) sendNotification(notificationManager, channelId, getApplicationContext());
        Log.d("ALARMNOTIF", "GOODEND");
        setWorker(getApplicationContext());
        return Result.success();
    }

    public static void setWorker(Context context){
        Log.d("TIME", "COUCOU");
        WorkManager workManager = WorkManager.getInstance(context);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(false)
                .build();

        List<Reminder> reminders = getReminders(context);

        long actualTime = System.currentTimeMillis();
        Long nextTime = getNextTime(reminders, actualTime);
        Log.d("TIME", String.valueOf(actualTime));
        if (nextTime == null) return;
        Log.d("TIME", String.valueOf(nextTime));
        Log.d("TIME", String.valueOf(nextTime - actualTime));

        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setConstraints(constraints)
                .setInitialDelay(nextTime - actualTime, TimeUnit.MILLISECONDS)
                .build();

        workManager.enqueue(work);
    }

    @Nullable
    private static Long getNextTime(List<Reminder> reminders, long actualTime) {
        long nextTime = Long.MAX_VALUE;
        long firstTime = Long.MAX_VALUE;

        for(Reminder reminder : reminders){
            long reminderTime = getMillisByStringHour(reminder.getHour());
            if(reminderTime == 0) return null;

            if(reminderTime > actualTime && nextTime > reminderTime){
                nextTime = reminderTime;
            }

            if(firstTime > reminderTime){
                firstTime = reminderTime;
            }
        }

        if(nextTime == Long.MAX_VALUE){
            nextTime = getNextDayOfTime(firstTime);
        }

        return nextTime;
    }

    private static long getNextDayOfTime(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        return calendar.getTimeInMillis();
    }

    private static List<Reminder> getReminders(Context context){
        DBManagement dbManagement = DBManagement.getInstance(context);
        User user = dbManagement.getUser();
        ReminderController reminderController = dbManagement.getReminderController();
        reminderController.open();
        List<Reminder> reminders = reminderController.getRemindersByIdUser(user.getId());
        reminderController.close();
        return reminders;
    }

    private static long getMillisByStringHour(String hour){
        String[] splitHour = hour.split("h");
        if(splitHour.length < 2) return 0;

        int hourValue = Integer.parseInt(splitHour[0]);
        int min = Integer.parseInt(splitHour[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourValue);
        calendar.set(Calendar.MINUTE, min);
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
