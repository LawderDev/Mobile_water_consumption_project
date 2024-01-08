package com.example.water_consumption_project.Services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.ServiceCompat;

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

    public void onCreate() {
        String channelId = "CHANNEL_ALARM";
        createNotificationChannel(channelId);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.menu)
                .setContentTitle("Running service for reminder !")
                .setContentText("Keep your good habits ! ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true).build();
        ServiceCompat.startForeground(this,100, notification, 0);
        NotificationAlarmReceiver.setRemindersAlarms(this);
    }

    private void createNotificationChannel(String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Alarm channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
