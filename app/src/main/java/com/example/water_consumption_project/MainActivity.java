package com.example.water_consumption_project;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.water_consumption_project.Controllers.ConsumptionController;
import com.example.water_consumption_project.Controllers.ReminderController;
import com.example.water_consumption_project.DataBase.DBManagement;
import com.example.water_consumption_project.Dialogs.DrinkDialog;
import com.example.water_consumption_project.Graphics.DayHistogram;
import com.example.water_consumption_project.Models.Consumption;
import com.example.water_consumption_project.Models.User;
import com.example.water_consumption_project.Styles.MainActivityStyle;
import com.github.mikephil.charting.charts.BarChart;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ConsumptionController consumptionController;
    private ReminderController reminderController;
    private User user;
    private TextView targetConsumptionText;
    private TextView currentConsumptionText;
    private DayHistogram dayHistogram;
    DrinkDialog dialog;

    MainActivityStyle mainActivityStyle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getApplicationContext().deleteDatabase("db_water_consumption");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initControllersAndUser();

        initGlobalTextViews();

        mainActivityStyle = new MainActivityStyle(this);
        mainActivityStyle.stylizingApp();

        initConsumptionsText();

        dayHistogram = new DayHistogram(findViewById(R.id.chart1), this);
        dayHistogram.refreshBarChart();

        manageDialogDrink();
        manageMenuButton();
        manageDrinkButton();
    }

    private void manageDialogDrink(){
        dialog = new DrinkDialog(MainActivity.this, user.getId(), consumptionController, currentConsumptionText, mainActivityStyle, dayHistogram);
    }

    private void initGlobalTextViews(){
        currentConsumptionText = findViewById(R.id.consumption_text);
        targetConsumptionText = findViewById(R.id.target_text);
    }

    private void initControllersAndUser(){
        DBManagement dbManagement = DBManagement.getInstance(this);
        consumptionController = dbManagement.getConsumptionController();
        reminderController = dbManagement.getReminderController();
        user = dbManagement.getUser();
    }

    private void initConsumptionsText() {
        currentConsumptionText.setText(String.format(getString(R.string.consumption), 0));
        refreshTargetConsumptionText();
    }



    private void manageMenuButton(){
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
        });
    }

    private void manageDrinkButton(){
        Button drinkButton = findViewById(R.id.drink_button);
        drinkButton.setOnClickListener((v) -> {
            dialog.show();
        });
    }


    private void refreshTargetConsumptionText(){
        String newText = String.format(getString(R.string.target), user.getTargetConsumption());
        targetConsumptionText.setText(newText);
    }

}