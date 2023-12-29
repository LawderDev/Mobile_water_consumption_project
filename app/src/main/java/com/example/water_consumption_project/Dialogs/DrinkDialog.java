package com.example.water_consumption_project.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.water_consumption_project.Controllers.ConsumptionController;
import com.example.water_consumption_project.DataBase.DBManagement;
import com.example.water_consumption_project.Graphics.DayHistogram;
import com.example.water_consumption_project.MainActivity;
import com.example.water_consumption_project.Models.User;
import com.example.water_consumption_project.R;
import com.example.water_consumption_project.Styles.MainActivityStyle;

import org.w3c.dom.Text;

public class DrinkDialog extends Dialog implements View.OnClickListener
{
    private Activity context;
    private int userId;
    private EditText drinkValueText;
    private ConsumptionController consumptionController;

    private Button drinkButton, removeButton, closeButton;

    private TextView currentConsumptionText;
    private MainActivityStyle mainActivityStyle;

    private DayHistogram dayHistogram;


    public DrinkDialog(Activity activity, int userId, ConsumptionController consumptionController, TextView currentConsumptionText, MainActivityStyle mainActivityStyle, DayHistogram dayHistogram) {
        super(activity);
        this.context = activity;
        this.userId = userId;
        this.consumptionController = consumptionController;
        this.currentConsumptionText = currentConsumptionText;
        this.mainActivityStyle = mainActivityStyle;
        this.dayHistogram = dayHistogram;
    }

    private void refreshCurrentConsumptionText(){
        int consumptionValue = consumptionController.getConsumptionValueByDateAndUser(System.currentTimeMillis(), userId);
        String currentConsumptionValueText = currentConsumptionText.getText().toString();
        Log.d("ENTER", currentConsumptionValueText);

        int currentConsumptionValue = Integer.parseInt(currentConsumptionValueText.split(" ")[0]);
        mainActivityStyle.refreshCurrentConsumptionTextAnimation(currentConsumptionText, currentConsumptionValue, consumptionValue);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.drink_dialog);
        drinkButton = (Button) findViewById(R.id.btn_drink);
        removeButton = (Button) findViewById(R.id.btn_remove);
        closeButton = (Button) findViewById(R.id.btn_close);
        drinkValueText = findViewById(R.id.drink_value);
        drinkButton.setOnClickListener(this);
        removeButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
    }

    private void addConsumption(int drinkValue, long date){
        consumptionController.open();
        consumptionController.insertConsumption(userId, date, drinkValue);
        refreshCurrentConsumptionText();
        consumptionController.close();
        dayHistogram.refreshBarChart();
        dismiss();
    }

    private void handleClose(){
        dismiss();
    }

    private void handleDrink(boolean isDrinking){
        long date = System.currentTimeMillis();
        int drinkValue = Integer.parseInt(drinkValueText.getText().toString());
        if(!isDrinking && consumptionController.getConsumptionValueByDateAndUser(date, userId) - drinkValue < 0) return;
        addConsumption(isDrinking ? drinkValue : -drinkValue, date);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_drink || id == R.id.btn_remove ){
            consumptionController.open();
           handleDrink(id == R.id.btn_drink);
            consumptionController.close();
           handleClose();
        } else if (id == R.id.btn_close) {
            handleClose();
        }
    }
}
