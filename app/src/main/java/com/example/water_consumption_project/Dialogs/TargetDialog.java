package com.example.water_consumption_project.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.example.water_consumption_project.Controllers.UserController;
import com.example.water_consumption_project.DataBase.DBManagement;
import com.example.water_consumption_project.R;

public class TargetDialog extends Dialog implements View.OnClickListener {
    private final int userId;
    private final UserController userController;
    private EditText targetConsumptionText;

    private OnTargetEditedListener onTargetEditedListener;
    public interface OnTargetEditedListener {
        void onTargetEdited();
    }

    public void setOnTargetEditedListener(OnTargetEditedListener listener) {
        this.onTargetEditedListener = listener;
    }
    public TargetDialog(Activity activity, UserController userController, int userId){
        super(activity);
        this.userController = userController;
        this.userId = userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.target_dialog);
        Button editButton = (Button) findViewById(R.id.btn_edit);
        Button closeButton = (Button) findViewById(R.id.btn_close);
        targetConsumptionText = findViewById(R.id.target_settings_value);
        editButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
    }

    private void handleClose(){
        dismiss();
    }

    private void handleEdit(){
        userController.open();
        int targetConsumptionValue = Integer.parseInt(targetConsumptionText.getText().toString());
        Log.d("TARGET", String.valueOf(targetConsumptionValue));
        userController.updateTargetConsumption(userId, targetConsumptionValue);
        DBManagement dbManagement = DBManagement.getInstance(this.getContext());
        dbManagement.updateUser();
        userController.close();

        if (onTargetEditedListener != null) {
            onTargetEditedListener.onTargetEdited();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_edit){
            handleEdit();
            handleClose();
        } else if (id == R.id.btn_close) {
            handleClose();
        }
    }
}
