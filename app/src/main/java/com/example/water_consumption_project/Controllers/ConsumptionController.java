package com.example.water_consumption_project.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.water_consumption_project.DataBase.DBWaterConsumption;
import com.example.water_consumption_project.Models.Consumption;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConsumptionController extends DBWaterConsumption {

    public ConsumptionController(Context context) {
        super(context);
    }

    public long insertConsumption(int idUser, long date, int currentConsumption) {
        ContentValues values = new ContentValues();
        values.put("idUser", idUser);
        values.put("date", date);
        values.put("currentConsumption", currentConsumption);
        return db.insert("Consumption", null, values);
    }

    public List<Consumption> getConsumptionsByDateAndUser(long date, int idUser) {
        String[] attributes = new String[]{"id", "idUser", "date", "currentConsumption"};
        String[] whereTab = new String[]{String.valueOf(getBeginDate(date)), String.valueOf(getEndDate(date)), String.valueOf(idUser)};

        Cursor cursor = db.query(
                "Consumption",
                attributes,
                "date BETWEEN ? AND ? AND idUser = ?",
                whereTab,
                null,
                null,
                null);

        return cursorToConsumptions(cursor);
    }

    private long getBeginDate(long date) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(date);
        setTimeToMidnight(calendar);

        return calendar.getTimeInMillis();
    }

    private long getEndDate(long date) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(date);
        setTimeToLastMillisecond(calendar);

        return calendar.getTimeInMillis();
    }

    public int getConsumptionValueByDateAndUser(long timestamp, int idUser){
        List<Consumption> consumptions = getConsumptionsByDateAndUser(timestamp, idUser);
        int consumptionValue = 0;
        for(Consumption consumption : consumptions){
            consumptionValue += consumption.getCurrentConsumption();
        }
        return consumptionValue;
    }

    private void setTimeToHour(Calendar calendar, int hour, int min, int sec, int ms) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, ms);
    }

    private void setTimeToMidnight(Calendar calendar) {
        setTimeToHour(calendar,0,0,0,0);
    }

    private void setTimeToLastMillisecond(Calendar calendar) {
        setTimeToHour(calendar, 23, 59,59,999);
    }

    private List<Consumption> cursorToConsumptions(Cursor cursor) {
        List<Consumption> consumptions = new ArrayList<>();

        if (cursor.getCount() == 0 || !cursor.moveToFirst())
            return consumptions;

        do {
            int idCursor = cursor.getColumnIndex("id");
            int idUserCursor = cursor.getColumnIndex("idUser");
            int idDateCursor = cursor.getColumnIndex("date");
            int currentConsumptionCursor = cursor.getColumnIndex("currentConsumption");

            if (idCursor < 0 && idDateCursor < 0 && currentConsumptionCursor < 0) return null;

            int id = cursor.getInt(idCursor);
            int idUser = cursor.getInt(idUserCursor);
            long idDate = cursor.getLong(idDateCursor);
            int currentConsumption = cursor.getInt(currentConsumptionCursor);

            Consumption consumption = new Consumption(id, idUser, idDate, currentConsumption);

            consumptions.add(consumption);
        } while (cursor.moveToNext());

        cursor.close();

        return consumptions;
    }
}
