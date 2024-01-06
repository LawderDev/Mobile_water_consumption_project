package com.example.water_consumption_project;

import static android.icu.number.NumberRangeFormatter.with;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.water_consumption_project.Controllers.ConsumptionController;
import com.example.water_consumption_project.Controllers.ReminderController;
import com.example.water_consumption_project.DataBase.DBManagement;
import com.example.water_consumption_project.Dialogs.DrinkDialog;
import com.example.water_consumption_project.Graphics.DayHistogram;
import com.example.water_consumption_project.Models.User;
import com.example.water_consumption_project.Services.NotificationService;
import com.example.water_consumption_project.Styles.MainActivityStyle;

import java.util.Calendar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TO DELETE AT THE END
        getApplicationContext().deleteDatabase("db_water_consumption");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initControllersAndUser();

        reminderController.open();
        reminderController.insertReminder(user.getId(), "18h15");
        reminderController.close();

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
        scheduleNotificationService();
    }

    private void scheduleNotificationService() {
        /*Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(NotificationService.class, 1, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build();

        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueue(periodicWorkRequest);*/

       /* OneTimeWorkRequest oneTimeWorkRequest =
                new OneTimeWorkRequest.Builder(NotificationService.class)
                        .build();

        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueue(oneTimeWorkRequest);*/

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, String.valueOf(1))
                .setSmallIcon(R.drawable.menu)
                .setContentTitle("test")
                .setContentText("test")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "test";
            String description = "test";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(String.valueOf(1), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        notificationManager.notify(0, builder.build());

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