package com.example.water_consumption_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.water_consumption_project.Controllers.ReminderController;
import com.example.water_consumption_project.Controllers.UserController;
import com.example.water_consumption_project.DataBase.DBManagement;
import com.example.water_consumption_project.Models.User;

public class MenuActivity extends AppCompatActivity {

    UserController userController;
    ReminderController reminderController;
    User user;
    TextView targetConsumptionText;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        DBManagement dbManagement = DBManagement.getInstance(getApplicationContext());
        user = dbManagement.getUser();
        userController = dbManagement.getUserController();
        reminderController = dbManagement.getReminderController();

        targetConsumptionText = findViewById(R.id.target_value);

        initTargetConsumptionText();

        ImageButton closeButton = findViewById(R.id.menu_button);

        closeButton.setOnClickListener((v) ->{
            finish();
        });
    }

    private void initTargetConsumptionText(){
        targetConsumptionText.setText(String.format(getString(R.string.menu_target_value), user.getTargetConsumption()));
    }
}
