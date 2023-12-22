package com.example.water_consumption_project;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.water_consumption_project.Controllers.ConsumptionController;
import com.example.water_consumption_project.Controllers.ReminderController;
import com.example.water_consumption_project.Controllers.UserController;
import com.example.water_consumption_project.Models.Consumption;
import com.example.water_consumption_project.Models.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ConsumptionController consumptionController;
    private UserController userController;
    private ReminderController reminderController;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getApplicationContext().deleteDatabase("db_water_consumption");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stylizingApp();

        userController = new UserController(this);
        consumptionController = new ConsumptionController(this);
        reminderController = new ReminderController(this);

        userController.open();
        user = userController.getFirstUser();

        if(user == null) {
            userController.insertUser("User", 2000);
            user = userController.getFirstUser();
        }

        userController.close();

        initInformation();

        // Changer nom utilisateur

        Button drinkButton = findViewById(R.id.drink_button);
        ImageButton menuButton = findViewById(R.id.menu_button);

        menuButton.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
        });

        drinkButton.setOnClickListener((v) -> {
            long date = System.currentTimeMillis();
            consumptionController.open();
            consumptionController.insertConsumption(user.getId(), date, 300);
            Toast.makeText(this, "DRINK !", Toast.LENGTH_LONG).show();
            Toast.makeText(this, String.valueOf(consumptionController.getConsumptionsByDateAndUser(date, user.getId()).get(0).getCurrentConsumption()), Toast.LENGTH_LONG).show();
            consumptionController.close();
            initInformation();
        });

    }

    private void initInformation() {
        TextView currentConsumptionText = findViewById(R.id.consumption_text);
        TextView targetConsumptionText = findViewById(R.id.target_text);

        currentConsumptionText.setText(getCurrentConsumption() + " ml");
        targetConsumptionText.setText("/ " +user.getTargetConsumption() + " ml");
    }

    private int getCurrentConsumption(){
        consumptionController.open();
        List<Consumption> consumptions =  consumptionController.getConsumptionsByDateAndUser(System.currentTimeMillis(), user.getId());
        consumptionController.close();
        int consumptionValue = 0;
        for(Consumption consumption : consumptions){
            consumptionValue += consumption.getCurrentConsumption();
        }
        return consumptionValue;
    }

    private void stylizingApp () {
        // Get the TextView
        TextView welcomeText = findViewById(R.id.welcome_user_text);
        TextView monitoringConsumptionText = findViewById(R.id.monitoring_consumption_text);

        applyTextBold(welcomeText, R.string.welcome_user, 8, 12);
        applyTextBold(monitoringConsumptionText, R.string.monitoring_of_consumption, 14, 25);

        BarChart barChart = findViewById(R.id.chart1);

        // Sample data (replace this with your actual data)
        List<BarEntry> dayEntries = getListDay();

        BarData barData = new BarData(setupBarDataSet(dayEntries));

        // Customize X-axis
        setupXAxis(barChart);

        setupBarChart(barChart, barData);
    }

    private List<BarEntry> getListDay() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, 10f));
        entries.add(new BarEntry(2, 15f));
        entries.add(new BarEntry(3, 20f));
        entries.add(new BarEntry(4, 12f));
        entries.add(new BarEntry(5, 25f));
        entries.add(new BarEntry(6, 25f));
        entries.add(new BarEntry(7, 25f));
        return entries;
    }

    private void setupXAxis(BarChart barChart){
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return "Day " + (int) value;
            }
        });
    }

    private BarDataSet setupBarDataSet(List<BarEntry> dayEntries){
        BarDataSet barDataSet = new BarDataSet(dayEntries, "Water Consumption");
        barDataSet.setColor(Color.rgb(10, 142, 217));
        return barDataSet;
    }

    private void addGoalLimitLine(BarChart barChart, float goal) {
        LimitLine limitLine = new LimitLine(goal, "Goal");
        limitLine.setLineColor(Color.RED);
        limitLine.setLineWidth(2f);
        limitLine.setTextColor(Color.BLACK);
        limitLine.setTextSize(12f);

        // Ajoutez la ligne de limite à l'axe Y (ajustez l'endroit en fonction de vos préférences)
        barChart.getAxisLeft().addLimitLine(limitLine);
    }

    private void setupBarChart(BarChart barChart, BarData barData){
        barChart.setData(barData);
        barChart.getDescription().setText("Water Consumption by Day");
        barChart.setFitBars(true);
        barChart.animateY(1500);
        barChart.invalidate();

        // Ajoutez la ligne de limite avec l'objectif de consommation
        float goalConsumption = 18f; // Remplacez ceci par votre objectif de consommation
        addGoalLimitLine(barChart, goalConsumption);
    }

    private void applyTextBold(TextView textView, int textResId, int start, int end) {
        SpannableString spannableString = new SpannableString(getString(textResId));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Typeface customTypeface = ResourcesCompat.getFont(this, R.font.raleway_bold);
            if (customTypeface != null) {
                spannableString.setSpan(new TypefaceSpan(customTypeface), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue)),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        textView.setText(spannableString);
    }
}