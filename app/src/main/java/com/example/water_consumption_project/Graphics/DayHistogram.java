package com.example.water_consumption_project.Graphics;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.SHORT;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.example.water_consumption_project.Controllers.ConsumptionController;
import com.example.water_consumption_project.DataBase.DBManagement;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DayHistogram {
    BarChart barChart;
    long[] sevenLastDays;
    ConsumptionController consumptionController;
    User user;
    public DayHistogram(BarChart barChart, Context context){
        this.barChart = barChart;
        this.sevenLastDays = getSevenLastDays();
        DBManagement dbManagement = DBManagement.getInstance(context);
        this.consumptionController = dbManagement.getConsumptionController();
        this.user = dbManagement.getUser();
    }

    public void refreshBarChart(){
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

        consumptionController.open();
        for (int i = 0; i < sevenLastDays.length; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(sevenLastDays[i]);
            entries.add(new BarEntry(calendar.get(DAY_OF_WEEK), consumptionController.getConsumptionValueByDateAndUser(sevenLastDays[i], user.getId())));
        }
        consumptionController.close();
        return entries;
    }

    private void setupXAxis(){
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(sevenLastDays[(int) value -1]);
                return calendar.getDisplayName(DAY_OF_WEEK, SHORT, Locale.US);
            }
        });
    }

    private BarDataSet setupBarDataSet(List<BarEntry> dayEntries){
        BarDataSet barDataSet = new BarDataSet(dayEntries, "Water Consumption");
        barDataSet.setColor(Color.rgb(10, 142, 217));
        return barDataSet;
    }

    private void setupBarChart(BarData barData){
        float goalConsumption = user.getTargetConsumption();
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.setDescription(null);
        barChart.animateY(1500);
        float maxYRange = barChart.getYMax();

        if(maxYRange < goalConsumption * 1.5f){
            barChart.setVisibleYRange(0, goalConsumption * 1.5f, YAxis.AxisDependency.LEFT);
        }

        barChart.invalidate();

        // Ajoutez la ligne de limite avec l'objectif de consommation
        addGoalLimitLine(goalConsumption);
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


}
