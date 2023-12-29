package com.example.water_consumption_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.water_consumption_project.Controllers.ReminderController;
import com.example.water_consumption_project.Controllers.UserController;
import com.example.water_consumption_project.DataBase.DBManagement;
import com.example.water_consumption_project.Dialogs.TargetDialog;
import com.example.water_consumption_project.Models.User;

public class MenuActivity extends AppCompatActivity {

    UserController userController;
    ReminderController reminderController;
    DBManagement dbManagement;
    TargetDialog dialog;
    User user;
    TextView targetConsumptionText;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        dbManagement = DBManagement.getInstance(getApplicationContext());
        user = dbManagement.getUser();
        userController = dbManagement.getUserController();
        reminderController = dbManagement.getReminderController();

        targetConsumptionText = findViewById(R.id.target_settings_value);

        manageTargetDialog();
        initTargetConsumptionText();

        ImageButton closeButton = findViewById(R.id.menu_button);
        Button editButton = findViewById(R.id.drink_button);

        editButton.setOnClickListener((v) -> {
            dialog.show();
        });

        closeButton.setOnClickListener((v) ->{
            setResult(RESULT_OK);
            finish();
        });
    }

    protected void refreshData() {
        dbManagement = DBManagement.getInstance(getApplicationContext());
        user = dbManagement.getUser();
        initTargetConsumptionText();
    }

    private void manageTargetDialog(){
        dialog = new TargetDialog(MenuActivity.this, userController, user.getId());
        dialog.setOnTargetEditedListener(this::refreshData);
    }

    private void initTargetConsumptionText(){
        targetConsumptionText.setText(String.format(getString(R.string.menu_target_value), user.getTargetConsumption()));
    }
}
