package com.example.water_consumption_project;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.LONG;
import static java.util.Calendar.SHORT;
import static java.util.Calendar.TUESDAY;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ConsumptionController consumptionController;
    private UserController userController;
    private ReminderController reminderController;
    private User user;
    private TextView targetConsumptionText;
    private TextView currentConsumptionText;
    private BarChart barChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getApplicationContext().deleteDatabase("db_water_consumption");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        currentConsumptionText = findViewById(R.id.consumption_text);
        targetConsumptionText = findViewById(R.id.target_text);

        stylizingApp();
        initConsumptionsText();

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
                refreshCurrentConsumptionText();
                refreshBarChart();
        });

    }

    private void initConsumptionsText() {
        currentConsumptionText.setText(String.format(getString(R.string.consumption), 0));
        refreshTargetConsumptionText();
    }

    private void refreshCurrentConsumptionTextAnimation(int current, int target) {
        ValueAnimator animator = ValueAnimator.ofInt(current, target);
        animator.setDuration(1000);

        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            String newText = String.format(getString(R.string.consumption), animatedValue);
            currentConsumptionText.setText(newText);
        });

        animator.start();
    }


    private void refreshCurrentConsumptionText(){
        int consumptionValue = getConsumptionValueByDate(System.currentTimeMillis());
        String currentConsumptionValueText = currentConsumptionText.getText().toString();
        Log.d("ENTER", currentConsumptionValueText);

        int currentConsumptionValue = Integer.parseInt(currentConsumptionValueText.split(" ")[0]);
        refreshCurrentConsumptionTextAnimation(currentConsumptionValue, consumptionValue);
    }

    private void refreshTargetConsumptionText(){
        String newText = String.format(getString(R.string.target), user.getTargetConsumption());
        targetConsumptionText.setText(newText);
    }

    private int getConsumptionValueByDate(long timestamp){
        consumptionController.open();
        List<Consumption> consumptions =  consumptionController.getConsumptionsByDateAndUser(timestamp, user.getId());
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


        barChart = findViewById(R.id.chart1);

        refreshBarChart();
    }

    private void refreshBarChart(){
        // Sample data (replace this with your actual data)
        List<BarEntry> dayEntries = getListDay();

        BarData barData = new BarData(setupBarDataSet(dayEntries));

        // Customize X-axis
        setupXAxis();

        setupBarChart(barData);
    }

    private long[] getSevenLastDays() {
        Calendar calendar = Calendar.getInstance();

        long currentTime = System.currentTimeMillis();

        // Créez un tableau pour stocker les timestamps des 6 derniers jours
        long[] timestamps = new long[7];
        for (int i = 0; i < 6; i++) {
            calendar.setTimeInMillis(currentTime);
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            timestamps[i] = calendar.getTimeInMillis();
        }

        // Ajoutez le timestamp de la date actuelle à la dernière position du tableau
        timestamps[6] = currentTime;

        return timestamps;
    }

    private List<BarEntry> getListDay() {
        List<BarEntry> entries = new ArrayList<>();
        long[] sevenLastDays = getSevenLastDays();
        for (int i = 0; i < sevenLastDays.length; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(sevenLastDays[i]);
            entries.add(new BarEntry(calendar.get(DAY_OF_WEEK), getConsumptionValueByDate(sevenLastDays[i])));
        }
        return entries;
    }

    private void setupXAxis(){
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        long[] sevenLastDays = getSevenLastDays();

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(sevenLastDays[(int) value]);
                return calendar.getDisplayName(DAY_OF_WEEK, SHORT, Locale.US);
            }
        });
    }

    private BarDataSet setupBarDataSet(List<BarEntry> dayEntries){
        BarDataSet barDataSet = new BarDataSet(dayEntries, "Water Consumption");
        barDataSet.setColor(Color.rgb(10, 142, 217));
        return barDataSet;
    }

    private void addGoalLimitLine(float goal) {
        LimitLine limitLine = new LimitLine(goal, "Goal");
        limitLine.setLineColor(Color.RED);
        limitLine.setLineWidth(2f);
        limitLine.setTextColor(Color.BLACK);
        limitLine.setTextSize(12f);

        // Ajoutez la ligne de limite à l'axe Y (ajustez l'endroit en fonction de vos préférences)
        barChart.getAxisLeft().addLimitLine(limitLine);
    }

    private void setupBarChart(BarData barData){
        float goalConsumption = user.getTargetConsumption();
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.animateY(1500);
        float maxYRange = barChart.getYMax();

        if(maxYRange < goalConsumption * 1.5f){
            barChart.setVisibleYRange(0, goalConsumption * 1.5f, YAxis.AxisDependency.LEFT);
        }

        barChart.invalidate();

        // Ajoutez la ligne de limite avec l'objectif de consommation
        addGoalLimitLine(goalConsumption);
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