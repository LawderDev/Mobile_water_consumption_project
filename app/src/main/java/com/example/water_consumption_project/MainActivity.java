package com.example.water_consumption_project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.airbnb.lottie.LottieAnimationView;
import com.example.water_consumption_project.Controllers.ConsumptionController;
import com.example.water_consumption_project.Controllers.ReminderController;
import com.example.water_consumption_project.DataBase.DBManagement;
import com.example.water_consumption_project.Dialogs.DrinkDialog;
import com.example.water_consumption_project.Graphics.DayHistogram;
import com.example.water_consumption_project.Models.Reminder;
import com.example.water_consumption_project.Models.User;
import com.example.water_consumption_project.Services.NotificationAlarmReceiver;
import com.example.water_consumption_project.Services.NotificationService;
import com.example.water_consumption_project.Services.NotificationWorker;
import com.example.water_consumption_project.Styles.MainActivityStyle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ConsumptionController consumptionController;
    private ReminderController reminderController;
    private User user;
    private TextView targetConsumptionText;
    private TextView currentConsumptionText;
    DBManagement dbManagement;
    private DayHistogram dayHistogram;
    DrinkDialog dialog;

    MainActivityStyle mainActivityStyle;
    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TO DELETE AT THE END
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
        manageNotificationsButton();
        manageDrinkButton();
        checkPostPermission();
        scheduleNotificationService();
    }


    private void scheduleNotificationService() {
       NotificationWorker.setWorker(this);
    }

    private void checkPostPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("SHOWNOTIF", "PERMISSION not granted, requesting...");
            // Demander la permission à l'utilisateur
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // La permission a été accordée, vous pouvez exécuter le code qui dépend de cette permission.
                Log.d("SHOWNOTIF", "Permission granted");
            } else {
                // La permission a été refusée, vous pouvez informer l'utilisateur ou prendre d'autres mesures.
                Log.d("SHOWNOTIF", "Permission denied");
            }
        }
    }

    private void manageNotificationsButton(){
        LottieAnimationView notificationButton = findViewById(R.id.bell_button);
        notificationButton.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
           startActivity(intent);
        });
    }

    private void manageDialogDrink(){
        dialog = new DrinkDialog(MainActivity.this, user.getId(), consumptionController, currentConsumptionText, mainActivityStyle, dayHistogram);
    }

    private void initGlobalTextViews(){
        currentConsumptionText = findViewById(R.id.consumption_text);
        targetConsumptionText = findViewById(R.id.target_text);
    }

    private void initControllersAndUser(){
        dbManagement = DBManagement.getInstance(this);
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
            someActivityResultLauncher.launch(intent);
        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        user = dbManagement.getUser();
                        refreshTargetConsumptionText();
                        dayHistogram.reloadDayHistogram();
                    }
                }
            });

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