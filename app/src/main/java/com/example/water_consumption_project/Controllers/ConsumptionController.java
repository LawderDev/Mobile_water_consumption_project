package com.example.water_consumption_project.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.water_consumption_project.DataBase.DBWaterConsumption;
import com.example.water_consumption_project.Models.Consumption;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        // !!!!! Remove and relauch database because of type mistake
        return db.insert("Consumption", null, values);
    }

    public List<Consumption> getConsumptionsByDate(long date) {
       /* Date currentDate = new Date(date);

        // Utilisation de Calendar pour extraire l'année, le mois et le jour
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Les mois commencent à partir de zéro
        int day = calendar.get(Calendar.DAY_OF_MONTH);*/
        String[] attributes = new String[]{"id", "idUser", "date", "currentConsumption"};
        String[] whereTab = new String[]{String.valueOf(date)};

        Cursor cursor = db.query(
                "Consumption",
                attributes,
                "date = ?",
                whereTab,
                null,
                null,
                null);

        return cursorToConsumptions(cursor);
    }

    private List<Consumption> cursorToConsumptions(Cursor cursor) {
        List<Consumption> consumptions = new ArrayList<>();

        if (cursor.getCount() == 0 || !cursor.moveToFirst())
            return null;

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
