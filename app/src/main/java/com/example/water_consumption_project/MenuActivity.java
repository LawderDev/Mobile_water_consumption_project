package com.example.water_consumption_project;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.water_consumption_project.Controllers.ReminderController;
import com.example.water_consumption_project.Controllers.UserController;
import com.example.water_consumption_project.DataBase.DBManagement;
import com.example.water_consumption_project.Dialogs.TargetDialog;
import com.example.water_consumption_project.Models.Reminder;
import com.example.water_consumption_project.Models.User;
import com.example.water_consumption_project.Services.NotificationWorker;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    UserController userController;
    ReminderController reminderController;
    DBManagement dbManagement;
    TargetDialog dialog;
    ImageButton reminderAddButton;
    User user;
    TextView targetConsumptionText;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        reminderAddButton = findViewById(R.id.add_reminder_button);

        dbManagement = DBManagement.getInstance(getApplicationContext());
        user = dbManagement.getUser();
        userController = dbManagement.getUserController();
        reminderController = dbManagement.getReminderController();

        getReminders();

        targetConsumptionText = findViewById(R.id.target_settings_value);

        manageTargetDialog();
        initTargetConsumptionText();

        ImageButton closeButton = findViewById(R.id.menu_button);
        Button editButton = findViewById(R.id.drink_button);

        reminderAddButton.setOnClickListener((v) -> {
            showTimePickerDialog();
        });

        editButton.setOnClickListener((v) -> {
            dialog.show();
        });

        closeButton.setOnClickListener((v) ->{
            setResult(RESULT_OK);
            finish();
        });
    }



    private void refreshData() {
        dbManagement = DBManagement.getInstance(getApplicationContext());
        user = dbManagement.getUser();
        initTargetConsumptionText();
    }

    private void manageTargetDialog(){
        dialog = new TargetDialog(MenuActivity.this, userController, user.getId());
        dialog.setOnTargetEditedListener(this::refreshData);
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        // Format de l'heure choisie en "hhmm"
                        String selectedTime = new SimpleDateFormat("HH'h'mm").format(calendar.getTime());
                        reminderController.open();
                        long idAdd = reminderController.insertReminder(user.getId(),selectedTime, true);
                        reminderController.close();
                        getReminders();
                        NotificationWorker.setWorker(getApplicationContext(), new Reminder((int) idAdd, user.getId(), selectedTime, true));
                    }
                }, currentHour, currentMinute, false);

        timePickerDialog.show();

    }
    private void initTargetConsumptionText(){
        targetConsumptionText.setText(String.format(getString(R.string.menu_target_value), user.getTargetConsumption()));
    }

    private void getReminders(){
        reminderController.open();
        List<Reminder> reminderList = reminderController.getRemindersByIdUser(user.getId());
        reminderAddButton.setVisibility(reminderList.size() == 7 ? View.GONE : View.VISIBLE);
        reminderController.close();
        LinearLayout reminderBloc = findViewById(R.id.reminder_bloc);
        reminderBloc.removeAllViews();
        for(Reminder reminder : reminderList) {
            String reminderHour = reminder.getHour();
            View reminderBox = LayoutInflater.from(this).inflate(R.layout.reminder_box, reminderBloc, false);
            MaterialTextView reminderTitle = reminderBox.findViewById(R.id.reminder_title);
            reminderTitle.setText(reminderHour);
            reminderBloc.addView(reminderBox);
            View reminderRemove = reminderBox.findViewById(R.id.x_1);
            reminderRemove.setOnClickListener(v -> {
                reminderBloc.removeView(reminderBox);
                reminderController.open();
                reminderController.removeReminderById(reminder.getId());
                reminderController.close();
                reminderAddButton.setVisibility(reminderList.size() - 1 == 7 ? View.GONE : View.VISIBLE);
            });
        }

    }
}
