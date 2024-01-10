package com.example.water_consumption_project;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.water_consumption_project.Controllers.ReminderController;
import com.example.water_consumption_project.DataBase.DBManagement;
import com.example.water_consumption_project.Models.Reminder;
import com.example.water_consumption_project.Models.User;
import com.example.water_consumption_project.Services.NotificationWorker;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity{
    ReminderController reminderController;
    DBManagement dbManagement;
    User user;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        dbManagement = DBManagement.getInstance(this);
        user = dbManagement.getUser();
        reminderController = dbManagement.getReminderController();

        ImageButton closeButton = findViewById(R.id.menu_button);

        reminderController.open();
        List<Reminder> reminders = reminderController.getRemindersByIdUser(user.getId());
        reminderController.close();
        LinearLayout notificationBoxContainer = findViewById(R.id.notification_box_container);

        for(Reminder reminder : reminders){
            long reminderMillis = NotificationWorker.getMillisByStringHour(reminder.getHour());
            if(System.currentTimeMillis() >= reminderMillis && reminder.getIsMissing()){
                View notificationBox = LayoutInflater.from(this).inflate(R.layout.notification_box, notificationBoxContainer, false);

                TextView notificationTitle = notificationBox.findViewById(R.id.notification_title);
                notificationTitle.setText(getString(R.string.reminder_of, reminder.getHour()));
                View removeButton =  notificationBox.findViewById(R.id.x_1);
                removeButton.setOnClickListener((v) -> {
                    reminderController.open();
                    reminderController.updateIsMissingById(reminder.getId(), false);
                    reminderController.close();
                    notificationBoxContainer.removeView(notificationBox);
                });
                notificationBoxContainer.addView(notificationBox);
            }
        }

        closeButton.setOnClickListener((v) ->{
            finish();
        });
    }
}
