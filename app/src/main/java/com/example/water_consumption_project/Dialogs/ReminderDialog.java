package com.example.water_consumption_project.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.water_consumption_project.Controllers.ReminderController;
import com.example.water_consumption_project.Controllers.UserController;
import com.example.water_consumption_project.DataBase.DBManagement;
import com.example.water_consumption_project.R;

public class ReminderDialog extends Dialog implements View.OnClickListener {
    private final int userId;
    private final ReminderController reminderController;
    public ReminderDialog(Activity activity, ReminderController reminderController, int userId){
        super(activity);
        this.reminderController = reminderController;
        this.userId = userId;
    }
    private void handleClose(){
        dismiss();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_set){
 //            handleSet();
            handleClose();
        } else if (id == R.id.btn_close) {
            handleClose();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.reminder_dialog);
        Button setButton = (Button) findViewById(R.id.btn_set);
        Button closeButton = (Button) findViewById(R.id.btn_close);
        setButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
    }

}
