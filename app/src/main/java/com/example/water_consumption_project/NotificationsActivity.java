package com.example.water_consumption_project;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.water_consumption_project.Controllers.ReminderController;
import com.example.water_consumption_project.Controllers.UserController;
import com.example.water_consumption_project.DataBase.DBManagement;
import com.example.water_consumption_project.Dialogs.TargetDialog;
import com.example.water_consumption_project.Models.User;

public class NotificationsActivity extends AppCompatActivity{
    ReminderController reminderController;
    DBManagement dbManagement;
    User user;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        dbManagement = DBManagement.getInstance(getApplicationContext());
        user = dbManagement.getUser();
        reminderController = dbManagement.getReminderController();

        ImageButton closeButton = findViewById(R.id.menu_button);

        closeButton.setOnClickListener((v) ->{
            finish();
        });
    }
}
