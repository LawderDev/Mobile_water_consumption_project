package com.example.water_consumption_project.Services;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.content.Context;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.NotificationCompat;
import androidx.work.Constraints;
import androidx.work.Data;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificationWorker extends Worker {
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    @NonNull
    @Override
    public Result doWork() {
        boolean isResetAction = getInputData().getBoolean("is_reset_action", false);
        Context context = getApplicationContext();
        if(isResetAction){
            resetAllNotifications(context);
            return Result.success();
        }
        int reminderId = getInputData().getInt("reminder_id", -1);
        String channelId = "channel01";

        NotificationManager notificationManager = createChannel(channelId, context);
        if(notificationManager != null) sendNotification(reminderId, notificationManager, channelId, context);
        return Result.success();
    }


    private void resetAllNotifications(Context context){
        DBManagement dbManagement = DBManagement.getInstance(getApplicationContext());
        User user = dbManagement.getUser();
        ReminderController reminderController = dbManagement.getReminderController();
        reminderController.open();
        List<Reminder> reminders = reminderController.getRemindersByIdUser(user.getId());
        for(Reminder reminder : reminders){
            reminderController.updateIsMissingById(reminder.getId(), true);
            setWorker(context, reminder);
        }
        reminderController.close();
        setResetNotificationsWorker(context);
    }

    public static void setWorker(Context context, Reminder reminder){
        WorkManager workManager = WorkManager.getInstance(context);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(false)
                .build();

        long actualTime = System.currentTimeMillis();

        Data.Builder data = new Data.Builder();

        data.putInt("reminder_id", reminder.getId());

        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setConstraints(constraints)
                .setInitialDelay(getMillisByStringHour(reminder.getHour()) - actualTime, TimeUnit.MILLISECONDS)
                .setInputData(data.build())
                .build();

        workManager.enqueue(work);
    }

    public static void setResetNotificationsWorker(Context context){
        WorkManager workManager = WorkManager.getInstance(context);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(false)
                .build();

        Data.Builder data = new Data.Builder();

        data.putBoolean("is_reset_action", true);

        long actualTime = System.currentTimeMillis();

        Calendar midnightCalendar  = Calendar.getInstance();
        midnightCalendar.add(Calendar.DAY_OF_WEEK, 1);
        midnightCalendar.set(Calendar.HOUR, 0);
        midnightCalendar.set(Calendar.MINUTE, 0);
        midnightCalendar.set(Calendar.SECOND, 0);

        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setConstraints(constraints)
                .setInitialDelay(midnightCalendar.getTimeInMillis() - actualTime, TimeUnit.MILLISECONDS)
                .setInputData(data.build())
                .build();

        workManager.enqueue(work);
    }

    @Nullable
    public static Long getNextTime(List<Reminder> reminders, long actualTime) {
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

    public static int getReminderIdByTime(List<Reminder> reminders, long time){
        for (Reminder reminder : reminders) {
            String nextTimeString = new SimpleDateFormat("HH'h'mm").format(time);
            if (reminder.getHour().equals(nextTimeString)) {
                return reminder.getId();
            }
        }
        return -1;
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

    public static long getMillisByStringHour(String hour){
        String[] splitHour = hour.split("h");
        if(splitHour.length < 2) return 0;

        int hourValue = Integer.parseInt(splitHour[0]);
        int min = Integer.parseInt(splitHour[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourValue);
        calendar.set(Calendar.MINUTE, min);
        return calendar.getTimeInMillis();
    }

    private void sendNotification(int reminderId, NotificationManager notificationManager, String channelId, Context context){
        int notificationId = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(new Date()));
        notificationManager.notify(notificationId, createBuilder(notificationId, reminderId, channelId, context).build());
    }

    private NotificationManager createChannel(String channelId, Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "test";
            String description = "test";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
            return notificationManager;
        }
        return null;
    }

    private NotificationCompat.Builder createBuilder(int notificationId, int reminderId, String channelId, Context context){
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mainIntent.putExtra("notification_clicked", true);
        mainIntent.putExtra("reminder_id", reminderId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, mainIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.menu)
                .setContentTitle("Don't forget to drink water !")
                .setContentText("Keep your good habits ! ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }
}
